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
import java.util.StringJoiner;
import java.util.TreeMap;

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
			SystemInfo info = new SystemInfo();
			builder.append("# Benchmark Results\n\n");
			builder.append("## System Information").append("\n\n");
			builder.append("- Publish Date: ").append(LocalDate.now().toString()).append("\n");
			builder.append("- OS: ").append(info.getOperatingSystem().toString()).append("\n");
			builder.append("- CPU: ").append(info.getHardware().getProcessor().getProcessorIdentifier().getName()).append("\n");
			builder.append("- Java Version: ").append(System.getProperty("java.vm.vendor")).append(" - ").append(System.getProperty("java.runtime.name")).append(" - ").append(System.getProperty("java.vendor.version")).append("\n");
			if(validation.isEmpty()) {
				builder.append("- Consistent Data: Yes").append("\n");
				findMetadata(results).ifPresent(T -> builder.append(T.toPrettyText()));
			}
			else {
				builder.append("- Consistent Data: No:\n");
				validation.forEach(T -> builder.append("\t- ").append(T).append("\n"));
			}
			builder.append("\n\n");
			builder.append("## Libraries Tested").append("\n");
			builder.append("- JOML: 1.10.9").append("\n");
			builder.append("- JOML2 Fields: BETA").append("\n");
			
			builder.append("\n\n");
			for(BenchmarkCollection collection : results.values()) {
				builder.append("## ").append(toPascalCase(collection.category()).replace("<br>", " ")).append("\n\n");
				for(Entry<String, List<Benchmark>> clazz : collection.benchmarks().entrySet()) {
					builder.append("### ").append(clazz.getKey()).append("\n\n");
					builder.append(generateTable(clazz.getValue())).append("\n");
				}
			}
			Files.writeString(outputFile, builder);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String generateTable(List<Benchmark> benchmarks) {
		if(benchmarks.isEmpty()) return "";
		benchmarks.sort(Comparator.comparing(Benchmark::function));
		List<String> header = generateHeader(benchmarks);
		List<List<String>> table = new ArrayList<>();
		table.add(generateHeaderNames(header));
		benchmarks.forEach(T -> table.add(T.generateRow(header)));
		List<ColumnSettings> settings = new ArrayList<>();
		settings.add(new ColumnSettings(1, 1, Alignment.CENTER));
		settings.add(new ColumnSettings(2, 2, Alignment.RIGHT));
		for(int i = 0,m=header.size();i<m;i++) {
			settings.add(new ColumnSettings(2, 2, 3, "<br>", Alignment.RIGHT, -1));
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
					collections.computeIfAbsent(category, BenchmarkCollection::new).get(clazz, function).add(lib, new Score(obj.getAsJsonObject("primaryMetric"), multiplier), new Metadata(obj));
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
		int largest = knownMetadata.values().stream().max(null).orElse(1);
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
		try(BufferedReader reader = Files.newBufferedReader(inputFile)) {
			for(JsonElement element : JsonParser.parseReader(reader).getAsJsonArray()) {
				JsonObject benchmark = element.getAsJsonObject();
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
			if(Objects.equals(element.get("benchmark").getAsString(), obj.get("benchmark").getAsString())) {
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
	
	private static List<String> generateHeaderNames(List<String> source) {
		List<String> result = new ArrayList<>();
		result.add("Function");
		source.stream().map(BenchmarkFormatter::toPascalCase).forEach(T -> result.add(T+"<br>Score"));
		return result;
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
	
	public record Benchmark(String function, Map<String, Score> scores, Map<String, Metadata> metadata) {
		public Benchmark(String function) {
			this(function, new HashMap<>(), new HashMap<>());
		}
		
		public void add(String lib, Score score, Metadata meta) {
			scores.put(lib, score);
			metadata.put(lib, meta);
		}
		
		public List<String> generateRow(List<String> libraries) {
			List<String> result = new ArrayList<>();
			result.add(function());
			libraries.forEach(T -> result.add(Optional.ofNullable(scores().get(T)).map(Score::toScore).orElse("N/A")));
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
	}
	
	public record Metadata(String version, int warmup, String warupTime, int iterations, String iterationTime) {
		public Metadata(JsonObject obj) {
			this(obj.get("jmhVersion").getAsString(), obj.get("warmupIterations").getAsInt(), obj.get("warmupTime").getAsString(), obj.get("measurementIterations").getAsInt(), obj.get("measurementTime").getAsString());
		}
		
		public String toPrettyText() {
			StringJoiner joiner = new StringJoiner("\t- ", "\t- ", "\n");
			joiner.add("jmh="+version()+"\n");
			joiner.add("Warmup Iterations="+warmup()+"\n");
			joiner.add("Warmup Time="+warupTime()+"\n");
			joiner.add("Iteratations="+iterations()+"\n");
			joiner.add("Iteration Time="+iterationTime());
			return joiner.toString();
		}
		
		public String toText() {
			return "[jmh="+version()+", warmCount="+warmup()+", warmTime="+warupTime()+", count="+iterations()+", time="+iterationTime()+"]";
		}
	}
}
 