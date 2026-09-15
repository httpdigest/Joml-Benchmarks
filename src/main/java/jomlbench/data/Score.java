package jomlbench.data;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;

import com.google.gson.JsonObject;

public record Score(double score, double error, String unit) {
	private static final DecimalFormat FORMAT = new DecimalFormat("###,##0.0##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	private static final String ALLOCATION_METRIC = "\u00B7gc.alloc.rate.norm";

	public Score(JsonObject obj, double multiplier) {
		this(obj.get("score").getAsDouble() / multiplier, obj.get("scoreError").getAsDouble() / multiplier, obj.get("scoreUnit").getAsString());
	}
	
	public String toScore() {
		return FORMAT.format(score())+" "+unit()+"<br>Error ± "+FORMAT.format(error());
	}
	
	public String toAllocation() {
		return FORMAT.format(score())+" "+unit();
	}
	
	
	public static Score readScore(JsonObject obj, double multiplier) {
		return Optional.ofNullable(obj.getAsJsonObject("primaryMetric")).map(T -> new Score(T, multiplier)).orElse(null);
	}
	
	public static Score readAllocation(JsonObject obj, double multiplier) {
		return Optional.ofNullable(obj.getAsJsonObject("secondaryMetrics")).map(T -> T.getAsJsonObject(ALLOCATION_METRIC)).map(T -> new Score(T, multiplier)).orElse(null);
	}
	
	public static double parseMultiplier(JsonObject obj) {
		return obj.has("params") && obj.getAsJsonObject("params").has("operationMultiplier") ? obj.getAsJsonObject("params").get("operationMultiplier").getAsDouble() : 1D;
	}
}