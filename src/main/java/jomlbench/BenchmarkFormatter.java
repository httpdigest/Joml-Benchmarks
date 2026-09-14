package jomlbench;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import jomlbench.TableGenerator.Alignment;
import jomlbench.TableGenerator.ColumnSettings;
import oshi.SystemInfo;

public class BenchmarkFormatter {
	private static final DecimalFormat FORMAT = new DecimalFormat("###,##0.0##");
	/** JMH prefixes secondary metrics with U+00B7; spelled as an escape so the source stays ASCII. */
	private static final String ALLOCATION_METRIC = "\u00B7gc.alloc.rate.norm";
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

	public static void main(String[] args) {
		processBenchmarks(args);
	}
	
	public static void generateVendorData() {
		Map<String, String> result = new HashMap<>();
		System.getProperties().forEach((K, V) -> result.put((String)K, (String)V));
		result.entrySet().stream().sorted(Comparator.comparing(Entry::getKey, String.CASE_INSENSITIVE_ORDER)).forEach(T -> System.out.println(T.getKey()+" -> "+T.getValue()));
	}
	
	private static void processBenchmarks(String[] args) {
		Path input = args.length < 1 ? Path.of("build/results/jmh/results.json") : Path.of(args[0]);
		Path tempFolder = args.length < 2 ? Path.of("build/results/joml") : Path.of(args[1]);
		Path outputFile = args.length < 3 ? Path.of("build/results/jomlresult/result.md") : Path.of(args[2]);
		try {
			if(Files.notExists(tempFolder)) Files.createDirectories(tempFolder);
			if(Files.notExists(outputFile.getParent())) Files.createDirectories(outputFile.getParent());
			split(input, tempFolder);
			Map<String, BenchmarkCollection> results = parse(tempFolder);
			List<String> validation = findInconsistencies(results);
			StringBuilder builder = new StringBuilder();
			builder.append("# Benchmark Results\n\n");
			builder.append("## System Information").append("\n\n");
			builder.append("- Publish Date: ").append(LocalDate.now().toString()).append("\n");
			// Everything below comes from the benchmark records themselves, not from this
			// JVM. The formatter may well run on a different machine, JDK or day than the
			// measurements did, and results/temp keeps results across runs.
			if(validation.isEmpty()) {
				builder.append("- Consistent Data: Yes").append("\n");
				findMetadata(results).ifPresent(T -> builder.append(T.toPrettyText()));
			}
			else {
				builder.append("- Consistent Data: No. These were not measured under the same conditions as the rest, so the tables below mix runs:\n");
				validation.forEach(T -> builder.append("\t- ").append(T).append("\n"));
			}
			builder.append("\n\n");
			builder.append("## Libraries Tested").append("\n");
			appendLibraries(builder, results);
			
			builder.append("\n\n");
			for(BenchmarkCollection collection : results.values()) {
				builder.append("## ").append(toPascalCase(collection.category()).replace("<br>", " ")).append("\n\n");
				for(Entry<String, List<Benchmark>> clazz : collection.benchmarks().entrySet()) {
					builder.append("### ").append(clazz.getKey()).append("\n\n");
					builder.append(generateTable(clazz.getValue(), false)).append("\n");
					String allocation = generateTable(clazz.getValue(), true);
					if(!allocation.isEmpty()) {
						builder.append("Allocation per operation:\n\n").append(allocation).append("\n");
					}
				}
			}
			Files.writeString(outputFile, builder);
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
		benchmarks.forEach(T -> table.add(allocation ? T.generateAllocationRow(header) : T.generateRow(header)));
		List<ColumnSettings> settings = new ArrayList<>();
		settings.add(new ColumnSettings(1, 1, Alignment.CENTER));
		settings.add(new ColumnSettings(2, 2, Alignment.RIGHT));
		for(int i = 0,m=header.size();i<m;i++) {
			// Allocation cells are a single line, timing cells carry score and error.
			settings.add(allocation ? new ColumnSettings(2, 2, Alignment.RIGHT) : new ColumnSettings(2, 2, 3, "<br>", Alignment.RIGHT, -1));
		}
		return TableGenerator.buildTable(table, settings, true);
	}
	
	private static Map<String, BenchmarkCollection> parse(Path file) throws IOException {
		List<Path> files = Files.isDirectory(file) ? Files.walk(file).filter(Files::isRegularFile).toList() : Collections.singletonList(file);
		Map<String, BenchmarkCollection> collections = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for(Path path : files) {
			try(BufferedReader reader = Files.newBufferedReader(path)) {
				for(JsonElement element : JsonParser.parseReader(reader).getAsJsonArray()) {
					JsonObject obj = element.getAsJsonObject();
					String[] benchmarkInfo = obj.get("benchmark").getAsString().split("\\.");
					String lib = benchmarkInfo[1];
					String category = benchmarkInfo[2];
					String clazz = benchmarkInfo[3];
					String function = benchmarkInfo[benchmarkInfo.length-1].substring(4);
					double multiplier = obj.has("params") && obj.getAsJsonObject("params").has("operationMultiplier") ? obj.getAsJsonObject("params").get("operationMultiplier").getAsDouble() : 1D;
					collections.computeIfAbsent(category, BenchmarkCollection::new).get(clazz, function).add(lib, new Score(obj.getAsJsonObject("primaryMetric"), multiplier), readAllocation(obj, multiplier), new Metadata(obj));
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return collections;
	}
	
	private static List<String> findInconsistencies(Map<String, BenchmarkCollection> map) {
		Map<Metadata, Integer> knownMetadata = map.values().stream().flatMap(T -> T.benchmarks().values().stream()).flatMap(List::stream).flatMap(T -> T.metadata().values().stream()).reduce(new HashMap<Metadata, Integer>(), (K, V) -> {
			K.merge(V, 1, Integer::sum);
			return K;
		}, (K, V) -> {
			K.putAll(V);
			return K;
		});
		if(knownMetadata.size() == 1) return List.of();
		int largest = knownMetadata.values().stream().max(Integer::compare).orElse(1);
		List<String> results = new ArrayList<>();
		for(BenchmarkCollection collection : map.values()) {
			for(Entry<String, List<Benchmark>> clazz : collection.benchmarks().entrySet()) {
				for(Benchmark mark : clazz.getValue()) {
					for(Entry<String, Metadata> meta : mark.metadata().entrySet()) {
						if(knownMetadata.get(meta.getValue()) == largest) continue;
						results.add(collection.category()+" => "+clazz.getKey()+" => "+mark.function()+" => "+meta.getKey()+": "+meta.getValue().toText());
					}
				}
			}
		}
		return results;
	}
	
	private static void split(Path inputFile, Path outputFolder) {
		System.out.println("Processing new Benchmark Results");
		int found = 0;
		Map<String, List<JsonObject>> objects = new LinkedHashMap<>();
		// The json carries the JDK and VM but not the OS or CPU, and this is the last point
		// at which we are still on the machine that ran them, so record it per entry.
		JsonObject environment = currentEnvironment();
		try(BufferedReader reader = Files.newBufferedReader(inputFile)) {
			for(JsonElement element : JsonParser.parseReader(reader).getAsJsonArray()) {
				JsonObject benchmark = element.getAsJsonObject();
				benchmark.add("_env", environment.deepCopy());
				objects.computeIfAbsent(benchmark.get("benchmark").getAsString().split("\\.")[1], _ -> new ArrayList<>()).add(benchmark);
				found++;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		Map<String, JsonArray> existing = read(outputFolder);
		System.out.println(String.format("Found %s Benchmarks", found));
		for(Entry<String, List<JsonObject>> entry : objects.entrySet()) {
			JsonArray array = existing.computeIfAbsent(entry.getKey(), _ -> new JsonArray());
			entry.getValue().forEach(T -> insert(array, T));
			try(JsonWriter writer = new JsonWriter(Files.newBufferedWriter(outputFolder.resolve(entry.getKey()+".json")))) {
				writer.setIndent("\t");
				Streams.write(array, writer);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Saved Benchmarks");
	}
	
	private static void insert(JsonArray array, JsonObject obj) {
		for(int i = 0,m=array.size();i<m;i++) {
			JsonObject element = array.get(i).getAsJsonObject();
			// @Param values produce several entries under one benchmark name, so both have
			// to match or all but the last would be dropped.
			if(Objects.equals(element.get("benchmark").getAsString(), obj.get("benchmark").getAsString())
					&& Objects.equals(element.get("params"), obj.get("params"))) {
				array.set(i, obj);
				return;
			}
		}
		array.add(obj);
	}
	
	private static Map<String, JsonArray> read(Path folder)  {
		Map<String, JsonArray> result = new HashMap<>();
		try {
			for(Path path : Files.walk(folder).filter(Files::isRegularFile).toList()) {
				String name = path.getFileName().toString();
				name = name.substring(0, name.length()-5);
				try(BufferedReader reader = Files.newBufferedReader(path)) {
					result.put(name, JsonParser.parseReader(reader).getAsJsonArray());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static List<String> generateHeader(List<Benchmark> list) {
		return list.stream().flatMap(T -> T.scores().keySet().stream()).distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList();
	}
	
	private static List<String> generateHeaderNames(List<String> source, String suffix) {
		List<String> result = new ArrayList<>();
		result.add("Function");
		source.stream().map(BenchmarkFormatter::toPascalCase).forEach(T -> result.add(T+"<br>"+suffix));
		return result;
	}
	
	private static void appendLibraries(StringBuilder builder, Map<String, BenchmarkCollection> results) {
		Map<String, String> properties = readProperties(Path.of("gradle.properties"));
		Set<String> libraries = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		results.values().stream().flatMap(T -> T.benchmarks().values().stream()).flatMap(List::stream).forEach(T -> libraries.addAll(T.scores().keySet()));
		for(String library : libraries) {
			String name = LIBRARY_NAMES.getOrDefault(library, toPascalCase(library).replace("<br>", " "));
			String version = properties.getOrDefault(LIBRARY_VERSIONS.getOrDefault(library, ""), "unknown");
			builder.append("- ").append(name).append(": ").append(version).append("\n");
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
	
	private static Score readAllocation(JsonObject obj, double multiplier) {
		if(!obj.has("secondaryMetrics")) return null;
		JsonObject secondary = obj.getAsJsonObject("secondaryMetrics");
		if(!secondary.has(ALLOCATION_METRIC)) return null;
		return new Score(secondary.getAsJsonObject(ALLOCATION_METRIC), multiplier);
	}
	
	private static JsonObject currentEnvironment() {
		JsonObject result = new JsonObject();
		try {
			SystemInfo info = new SystemInfo();
			result.addProperty("os", info.getOperatingSystem().toString());
			result.addProperty("cpu", info.getHardware().getProcessor().getProcessorIdentifier().getName().trim());
		}
		catch(Exception e) {
			result.addProperty("os", "unrecorded");
			result.addProperty("cpu", "unrecorded");
		}
		result.addProperty("measured", LocalDate.now().toString());
		return result;
	}
	
	private static String jsonString(JsonObject obj, String key, String fallback) {
		return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : fallback;
	}
	
	private static int jsonInt(JsonObject obj, String key, int fallback) {
		return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsInt() : fallback;
	}
	
	private static String jsonEnvironment(JsonObject obj, String key) {
		return obj.has("_env") ? jsonString(obj.getAsJsonObject("_env"), key, "unrecorded") : "unrecorded";
	}
	
	private static Optional<Metadata> findMetadata(Map<String, BenchmarkCollection> result) {
		return result.values().stream().flatMap(T -> T.benchmarks().values().stream()).flatMap(T -> T.stream()).flatMap(T -> T.metadata().values().stream()).findFirst();
	}
	
	private static String firstLetterUppercase(String string) {
		if(string == null || string.isEmpty()) return "";
		String first = Character.toString(string.charAt(0));
		return string.replaceFirst(first, first.toUpperCase());
	}
	
	private static String toPascalCase(String input) {
		StringBuilder builder = new StringBuilder();
		for(String s : input.replaceAll("_", " ").split(" ")) {
			builder.append(firstLetterUppercase(s)).append("<br>");
		}
		return builder.substring(0, builder.length() - 4);
	}
	
	public record BenchmarkCollection(String category, Map<String, List<Benchmark>> benchmarks) {
		public BenchmarkCollection(String category) {
			this(category, new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
		}
		
		public Benchmark get(String clazz, String function) {
			List<Benchmark> benchmark = benchmarks.computeIfAbsent(clazz, _ -> new ArrayList<>());
			for(int i = 0,m=benchmark.size();i<m;i++) {
				Benchmark mark = benchmark.get(i);
				if(mark.function().equals(function)) return mark;
			}
			Benchmark mark = new Benchmark(function);
			benchmark.add(mark);
			return mark;
		}
		
		public void add(String clazz, Benchmark result) {
			benchmarks.computeIfAbsent(clazz, _ -> new ArrayList<>()).add(result);
		}
	}
	
	public record Benchmark(String function, Map<String, Score> scores, Map<String, Score> allocations, Map<String, Metadata> metadata) {
		public Benchmark(String function) {
			this(function, new HashMap<>(), new HashMap<>(), new HashMap<>());
		}
		
		public void add(String lib, Score score, Score allocation, Metadata meta) {
			scores.put(lib, score);
			if(allocation != null) allocations.put(lib, allocation);
			metadata.put(lib, meta);
		}
		
		public List<String> generateRow(List<String> libraries) {
			List<String> result = new ArrayList<>();
			result.add(function());
			libraries.forEach(T -> result.add(Optional.ofNullable(scores().get(T)).map(Score::toScore).orElse("N/A")));
			return result;
		}
		
		public List<String> generateAllocationRow(List<String> libraries) {
			List<String> result = new ArrayList<>();
			result.add(function());
			libraries.forEach(T -> result.add(Optional.ofNullable(allocations().get(T)).map(Score::toAllocation).orElse("N/A")));
			return result;
		}
	}
	
	public record Score(double score, double error, String unit) {
		public Score(JsonObject obj, double multiplier) {
			this(obj.get("score").getAsDouble() / multiplier, obj.get("scoreError").getAsDouble() / multiplier, obj.get("scoreUnit").getAsString());
		}
		
		public String toScore() {
			return FORMAT.format(score())+" "+unit()+"<br>Error "+FORMAT.format(error())+" ±";
		}
		
		public String toAllocation() {
			return FORMAT.format(score())+" "+unit();
		}
	}
	
	/**
	 * Everything that has to match for two rows to belong in the same table. Record
	 * equality drives findInconsistencies, so adding a field here automatically makes
	 * a mismatch in it show up as "Consistent Data: No" rather than passing silently.
	 */
	public record Metadata(String version, String jdk, String vm, String vmVersion, int threads, int forks, int warmup, String warupTime, int iterations, String iterationTime, String os, String cpu) {
		public Metadata(JsonObject obj) {
			this(jsonString(obj, "jmhVersion", "unrecorded"), jsonString(obj, "jdkVersion", "unrecorded"), jsonString(obj, "vmName", "unrecorded"), jsonString(obj, "vmVersion", "unrecorded"),
					jsonInt(obj, "threads", 1), jsonInt(obj, "forks", 1),
					jsonInt(obj, "warmupIterations", 0), jsonString(obj, "warmupTime", "unrecorded"),
					jsonInt(obj, "measurementIterations", 0), jsonString(obj, "measurementTime", "unrecorded"),
					jsonEnvironment(obj, "os"), jsonEnvironment(obj, "cpu"));
		}
		
		public String toPrettyText() {
			StringJoiner joiner = new StringJoiner("\t- ", "\t- ", "\n");
			joiner.add("OS="+os()+"\n");
			joiner.add("CPU="+cpu()+"\n");
			joiner.add("JDK="+jdk()+" ("+vm()+" "+vmVersion()+")\n");
			joiner.add("jmh="+version()+"\n");
			joiner.add("Threads="+threads()+"\n");
			joiner.add("Forks="+forks()+"\n");
			joiner.add("Warmup Iterations="+warmup()+"\n");
			joiner.add("Warmup Time="+warupTime()+"\n");
			joiner.add("Iteratations="+iterations()+"\n");
			joiner.add("Iteration Time="+iterationTime());
			return joiner.toString();
		}
		
		public String toText() {
			return "[os="+os()+", cpu="+cpu()+", jdk="+jdk()+", vm="+vmVersion()+", jmh="+version()+", threads="+threads()+", forks="+forks()+", warmCount="+warmup()+", warmTime="+warupTime()+", count="+iterations()+", time="+iterationTime()+"]";
		}
	}
}
 