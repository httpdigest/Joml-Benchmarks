package jomlbench.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public record BenchmarkCollection(String category, Map<String, List<Benchmark>> benchmarks) {
	private static final Collector<Metadata, ?, Map<Metadata, Integer>> COLLECTOR = Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue));
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
	
	public Stream<Benchmark> flatten() {
		return benchmarks().values().stream().flatMap(List::stream);
	}
	
	public Stream<Metadata> flattenMetadata() {
		return flatten().flatMap(T -> T.metadata().values().stream());
	}
	
	public static Map<String, BenchmarkCollection> parse(Path file) throws IOException {
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
					double multiplier = Score.parseMultiplier(obj);
					collections.computeIfAbsent(category, BenchmarkCollection::new).get(clazz, function).add(lib, Score.readScore(obj, multiplier), Score.readAllocation(obj, multiplier), new Metadata(obj));
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return collections;
	}
	
	public static List<String> findInconsistencies(Map<String, BenchmarkCollection> map) {
		Map<Metadata, Integer> knownMetadata = map.values().stream().flatMap(BenchmarkCollection::flattenMetadata).collect(COLLECTOR);
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
}