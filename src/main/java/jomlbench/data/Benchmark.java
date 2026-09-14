package jomlbench.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		libraries.forEach(T -> result.add(Optional.ofNullable((allocation ? allocations() : scores()).get(T)).map(Score::toScore).orElse("N/A")));
		return result;
	}
}