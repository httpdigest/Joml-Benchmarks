package jomlbench.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public record Benchmark(String function, Map<String, Score> scores, Map<String, Score> allocations, Map<String, Metadata> metadata) {
	public Benchmark(String function) {
		this(function, new HashMap<>(), new HashMap<>(), new HashMap<>());
	}
	
	public void add(String lib, Score score, Score allocation, Metadata meta) {
		if(score != null) scores.put(lib, score);
		if(allocation != null) allocations.put(lib, allocation);
		metadata.put(lib, meta);
	}
	
	public List<String> generateRow(List<String> libraries, boolean allocation) {
		List<String> result = new ArrayList<>();
		result.add(function());
		// The allocation table has no error column and its own column settings, so it needs
		// toAllocation; formatting it with toScore appended a meaningless "Error 0.0" to
		// every cell.
		Map<String, Score> values = allocation ? allocations() : scores();
		Function<Score, String> formatter = allocation ? Score::toAllocation : Score::toScore;
		libraries.forEach(T -> result.add(Optional.ofNullable(values.get(T)).map(formatter).orElse("N/A")));
		return result;
	}
}