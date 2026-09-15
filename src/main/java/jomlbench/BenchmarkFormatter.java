package jomlbench;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import jomlbench.TableGenerator.Alignment;
import jomlbench.TableGenerator.ColumnSettings;
import jomlbench.data.Benchmark;
import jomlbench.data.BenchmarkCollection;
import jomlbench.data.Metadata;
import jomlbench.utils.JsonPreprocessor;
import jomlbench.utils.TextUtil;

public class BenchmarkFormatter {
	private static final Map<String, String> LIBRARY_NAMES = Map.of(
			"joml", "JOML",
			"joml2_fields", "JOML2 Fields",
			"joml2_records", "JOML2 Records",
			"lidiuma_math", "Lidiuma Math");
	private static final Map<String, String> LIBRARY_VERSIONS = Map.of(
			"joml", "JOML_VERSION",
			"joml2_fields", "JOML2_VERSION",
			"joml2_records", "JOML2_VERSION",
			"lidiuma_math", "LIDIUMA_MATH_VERSION");
	private static final Map<String, String> LIBRARY_NOTES = Map.of(
			"lidiuma_math", "components are boxed - Vec3F32, QuaternionF32 and Affine3F32 are records of java.lang.Float - so its rows include boxing and wrapper construction rather than float math alone.");
	/**
	 * Footnotes for rows that are not a like-for-like comparison, keyed by class name or by
	 * "ClassName.FunctionName". A gap or a mismatched algorithm that only lives in a source
	 * comment is invisible to whoever reads the published table, so it belongs here.
	 */
	private static final Map<String, String> NOTES = Map.of(
			"Vector3fBenchmarks.Angle", "**Angle** does not compare one algorithm. JOML and Lidiuma Math both take the acos of the clamped cosine with an exact `java.lang.Math.acos`, which neither library routes through fastmath; JOML2's `angleBetween` is built on atan2, which fastmath *does* approximate (error against `java.lang.Math` around 7e-5). JOML2 has no `angleCos`, so the row cannot be made like-for-like and the JOML2 figures are not a straight speedup. Lidiuma Math has no vector-to-vector angle function at all, so its column runs code written in the benchmark that mirrors JOML's algorithm.",
			"Matrix4x3fBenchmarks.BoneAnimation", "**BoneAnimation** transforms `operationMultiplier` (100) bones per invocation, and the score and allocation are divided by that. This row is therefore per bone, while every other row in the table is per call.",
			"Matrix4x3fBenchmarks.StandardOperation", "**StandardOperation** is N/A for Lidiuma Math because it has no incremental transform API. Composing it from `fromTranslation`/`fromRotation`/`fromScale` and two multiplies would put three matrix constructions and two full multiplies against JOML's fused in-place update - a different algorithm rather than a slower one.",
			"Matrix4fBenchmarks", "Lidiuma Math has no column here: its `Matrix4F32` offers no TRS composition and no position transform (only `multiply(Matrix4F32, Vec4F32)`), so these four functions cannot be written with library calls.");

	public static void main(String[] args) {
		processBenchmarks(args);
	}
	
	private static void processBenchmarks(String[] args) {
		Path input = args.length < 1 ? Path.of("build/results/jmh/results.json") : Path.of(args[0]);
		Path tempFolder = args.length < 2 ? Path.of("build/results/joml") : Path.of(args[1]);
		Path outputFile = args.length < 3 ? Path.of("build/results/jomlresult/result.md") : Path.of(args[2]);
		try {
			if(Files.notExists(tempFolder)) Files.createDirectories(tempFolder);
			if(Files.notExists(outputFile.getParent())) Files.createDirectories(outputFile.getParent());
			JsonPreprocessor.split(input, tempFolder);
			System.out.println("Parsing Benchmarks");
			Map<String, BenchmarkCollection> results = BenchmarkCollection.parse(tempFolder);
			System.out.println("Validating Benchmarks");
			List<String> validation = BenchmarkCollection.findInconsistencies(results);
			StringBuilder builder = new StringBuilder();
			builder.append("# Benchmark Results\n\n");
			builder.append("## System Information").append("\n\n");
			builder.append("- Publish Date: ").append(LocalDate.now().toString()).append("\n");
			// Everything below comes from the benchmark records themselves, not from this
			// JVM. The formatter may well run on a different machine, JDK or day than the
			// measurements did, and results/temp keeps results across runs.
			if(validation.isEmpty()) {
				builder.append("- Consistent Data: Yes").append("\n");
				findMetadata(results).map(Metadata::toPrettyText).ifPresent(builder::append);
			}
			else {
				builder.append("- Consistent Data: No. These were not measured under the same conditions as the rest, so the tables below mix runs:\n");
				validation.forEach(T -> builder.append("\t- ").append(T).append("\n"));
				// Without this the report loses its system information entirely in exactly the
				// case where knowing which machine produced which row matters most.
				builder.append("- Conditions present, largest group first:\n");
				BenchmarkCollection.distinctMetadata(results).forEach(T -> builder.append("\t- ").append(T.toText()).append("\n"));
			}
			builder.append("\n\n");
			builder.append("## Libraries Tested").append("\n");
			appendLibraries(builder, results);
			
			builder.append("\n\n");
			System.out.println("Generating Tables");
			for(BenchmarkCollection collection : results.values()) {
				builder.append("## ").append(TextUtil.toPascalCase(collection.category(), " ")).append("\n\n");
				for(Entry<String, List<Benchmark>> clazz : collection.benchmarks().entrySet()) {
					builder.append("### ").append(clazz.getKey()).append("\n\n");
					builder.append(generateTable(clazz.getValue(), false)).append("\n");
					String allocation = generateTable(clazz.getValue(), true);
					if(!allocation.isEmpty()) {
						builder.append("Allocation per operation:\n\n").append(allocation).append("\n");
					}
					appendNotes(builder, clazz.getKey(), clazz.getValue());
				}
			}
			System.out.println("Writing Output");
			Files.writeString(outputFile, builder);
			System.out.println("Done!");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String generateTable(List<Benchmark> benchmarks, boolean allocation) {
		if(benchmarks.isEmpty()) return "";
		if(allocation && benchmarks.stream().allMatch(T -> T.allocations().isEmpty())) return "";
		benchmarks.sort(Comparator.comparing(Benchmark::function));
		List<String> header = generateHeader(benchmarks);
		List<List<String>> table = new ArrayList<>();
		table.add(generateHeaderNames(header, allocation ? "Alloc" : "Score"));
		benchmarks.forEach(T -> table.add(T.generateRow(header, allocation)));
		List<ColumnSettings> settings = new ArrayList<>();
		settings.add(new ColumnSettings(1, 1, Alignment.CENTER));
		settings.add(new ColumnSettings(2, 2, Alignment.RIGHT));
		for(int i = 0,m=header.size();i<m;i++) {
			settings.add(allocation ? new ColumnSettings(2, 2, Alignment.RIGHT) : new ColumnSettings(2, 2, 3, "<br>", Alignment.RIGHT, -1));
		}
		return TableGenerator.buildTable(table, settings, true);
	}
	
	private static List<String> generateHeader(List<Benchmark> list) {
		return list.stream().flatMap(T -> T.scores().keySet().stream()).distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList();
	}
	
	private static List<String> generateHeaderNames(List<String> source, String suffix) {
		List<String> result = new ArrayList<>();
		result.add("Function");
		source.stream().map(TextUtil::toPascalCase).forEach(T -> result.add(T+"<br>"+suffix));
		return result;
	}
	
	private static void appendNotes(StringBuilder builder, String clazz, List<Benchmark> benchmarks) {
		List<String> notes = new ArrayList<>();
		// generateTable has already sorted the list, so the footnotes come out in table order.
		Optional.ofNullable(NOTES.get(clazz)).ifPresent(notes::add);
		benchmarks.stream().map(T -> NOTES.get(clazz+"."+T.function())).filter(Objects::nonNull).forEach(notes::add);
		if(notes.isEmpty()) return;
		builder.append("Notes:\n\n");
		notes.forEach(T -> builder.append("- ").append(T).append("\n"));
		builder.append("\n");
	}
	
	private static void appendLibraries(StringBuilder builder, Map<String, BenchmarkCollection> results) {
		Map<String, String> properties = readProperties(Path.of("gradle.properties"));
		Set<String> libraries = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		results.values().stream().flatMap(BenchmarkCollection::flatten).forEach(T -> libraries.addAll(T.scores().keySet()));
		for(String library : libraries) {
			String name = LIBRARY_NAMES.getOrDefault(library, TextUtil.toPascalCase(library, " "));
			String version = properties.getOrDefault(LIBRARY_VERSIONS.getOrDefault(library, ""), "unknown");
			builder.append("- ").append(name).append(": ").append(version).append("\n");
			Optional.ofNullable(LIBRARY_NOTES.get(library)).ifPresent(T -> builder.append("\t- ").append(T).append("\n"));
		}
	}
	
	private static Map<String, String> readProperties(Path path) {
		Map<String, String> result = new HashMap<>();
		try(BufferedReader reader = Files.newBufferedReader(path)) {
			Properties properties = new Properties();
			properties.load(reader);
			properties.forEach((K, V) -> result.put((String)K, ((String)V).trim()));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static Optional<Metadata> findMetadata(Map<String, BenchmarkCollection> result) {
		return result.values().stream().flatMap(BenchmarkCollection::flattenMetadata).findFirst();
	}
}
 