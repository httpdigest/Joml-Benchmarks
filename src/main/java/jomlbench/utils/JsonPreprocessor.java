package jomlbench.utils;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import oshi.SystemInfo;

public class JsonPreprocessor {
	public static void split(Path inputFile, Path outputFolder) {
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
}
