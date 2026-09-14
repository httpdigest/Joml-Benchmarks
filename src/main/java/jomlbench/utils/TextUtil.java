package jomlbench.utils;

public class TextUtil {
	private static String firstLetterUppercase(String string) {
		if(string == null || string.isEmpty()) return "";
		String first = Character.toString(string.charAt(0));
		return string.replaceFirst(first, first.toUpperCase());
	}
	
	public static String toPascalCase(String input) {
		return toPascalCase(input, "<br>");
	}
	
	public static String toPascalCase(String input, String splitter) {
		StringBuilder builder = new StringBuilder();
		for(String s : input.replaceAll("_", " ").split(" ")) {
			builder.append(firstLetterUppercase(s)).append(splitter);
		}
		return builder.substring(0, builder.length() - splitter.length());
	}
}
