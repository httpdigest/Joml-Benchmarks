package jomlbench.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Everything that has to match for two rows to belong in the same table. Record
 * equality drives findInconsistencies, so adding a field here automatically makes
 * a mismatch in it show up as "Consistent Data: No" rather than passing silently.
 */
public record Metadata(String version, String jdk, String vm, String vmVersion, int threads, int forks, int warmup, String warmupTime, int iterations, String iterationTime, String os, String cpu, String libraryOptions) {
	public Metadata(JsonObject obj) {
		this(
			jsonString(obj, "jmhVersion", "unrecorded"), 
			jsonString(obj, "jdkVersion", "unrecorded"), 
			jsonString(obj, "vmName", "unrecorded"), 
			jsonString(obj, "vmVersion", "unrecorded"),
			jsonInt(obj, "threads", 1), 
			jsonInt(obj, "forks", 1),
			jsonInt(obj, "warmupIterations", 0), 
			jsonString(obj, "warmupTime", "unrecorded"),
			jsonInt(obj, "measurementIterations", 0), 
			jsonString(obj, "measurementTime", "unrecorded"),
			jsonEnvironment(obj, "os"), 
			jsonEnvironment(obj, "cpu"), 
			libraryOptions(obj));
	}
	
	public String toPrettyText() {
		StringJoiner joiner = new StringJoiner("\t- ", "\t- ", "\n");
		joiner.add("OS="+os()+"\n");
		joiner.add("CPU="+cpu()+"\n");
		joiner.add("JDK="+jdk()+" ("+vm()+" "+vmVersion()+")\n");
		joiner.add("Library options="+libraryOptions()+"\n");
		joiner.add("jmh="+version()+"\n");
		joiner.add("Threads="+threads()+"\n");
		joiner.add("Forks="+forks()+"\n");
		joiner.add("Warmup Iterations="+warmup()+"\n");
		joiner.add("Warmup Time="+warmupTime()+"\n");
		joiner.add("Iterations="+iterations()+"\n");
		joiner.add("Iteration Time="+iterationTime());
		return joiner.toString();
	}
	
	public String toText() {
		return "[os="+os()+", cpu="+cpu()+", jdk="+jdk()+", vm="+vmVersion()+", opts="+libraryOptions()+", jmh="+version()+", threads="+threads()+", forks="+forks()+", warmCount="+warmup()+", warmTime="+warmupTime()+", count="+iterations()+", time="+iterationTime()+"]";
	}
	
	static String jsonEnvironment(JsonObject obj, String key) {
		return obj.has("_env") ? jsonString(obj.getAsJsonObject("_env"), key, "unrecorded") : "unrecorded";
	}
	
	static String jsonString(JsonObject obj, String key, String fallback) {
		return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : fallback;
	}
	
	static int jsonInt(JsonObject obj, String key, int fallback) {
		return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsInt() : fallback;
	}
	
	static String libraryOptions(JsonObject obj) {
		if(!obj.has("jvmArgs") || !obj.get("jvmArgs").isJsonArray()) return "unrecorded";
		List<String> options = new ArrayList<>();
		for(JsonElement element : obj.getAsJsonArray("jvmArgs")) {
			String arg = element.getAsString();
			if(arg.startsWith("-Djoml.")) options.add(arg.substring(2));
		}
		if(options.isEmpty()) return "defaults";
		Collections.sort(options);
		return options.toString();
	}
}