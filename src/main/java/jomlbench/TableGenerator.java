package jomlbench;

import java.util.List;

public class TableGenerator {
	
	public static String buildTable(List<List<String>> table, List<ColumnSettings> settings, boolean firstIsHeader) {
		if(table.isEmpty()) return "";
		int size = validateTable(table, settings, firstIsHeader);
		calculateColumnWidth(table, settings, firstIsHeader, size);
		
		StringBuilder builder = new StringBuilder();
		if(firstIsHeader) {
			ColumnSettings mainSetting = settings.get(0);
			List<String> header = table.get(0);
			for(int j = 0,n=header.size();j<n;j++) {
				ColumnSettings setting = settings.get(j+1);
				String element = header.get(j);
				builder.append("|").append(setting.buildEmpty());
				int start = builder.length() - setting.getAlignOffset(element.length(), mainSetting.align());
				builder.replace(start, start+element.length(), element);
			}
			builder.append("|\n");
			for(int j = 0,n=header.size();j<n;j++) {
				ColumnSettings setting = settings.get(j+1);
				builder.append("|").append(setting.buildSeperator());
			}
			builder.append("|\n");
		}
		
		int firstIndex = firstIsHeader ? 1 : 0;
		for(int i = firstIndex,m=table.size();i<m;i++) {
			List<String> row = table.get(i);
			for(int j = 0,n=row.size();j<n;j++) {
				ColumnSettings setting = settings.get(j+firstIndex);
				String element = row.get(j);
				builder.append("|").append(setting.buildEmpty());
				if(setting.minRight() > 0) {
					String[] split = element.split(setting.minimumIndicator, 2);
					if(split.length != 2) split = new String[] {element, ""};
					int length = split[0].length()+2+Math.max(split[1].length(), setting.minRight());
					int start = builder.length() - setting.getAlignOffset(length);
					builder.replace(start, start+element.length(), element);
				}
				else {
					int start = builder.length() - setting.getAlignOffset(element.length());
					builder.replace(start, start+element.length(), element);
				}
			}
			builder.append("|\n");
		}
		
		return builder.toString();
	}
	
	private static int validateTable(List<List<String>> table, List<ColumnSettings> settings, boolean firstIsHeader) {
		int size = table.get(0).size();
		for(int i = 0,m=table.size();i<m;i++) {
			if(table.get(i).size() != size) throw new IllegalStateException("Table Index ["+i+"] Index isn't the same Column size as the rest!");
		}
		if(size != settings.size() - (firstIsHeader ? 1 : 0)) throw new IllegalStateException("Settings["+settings.size()+"] size isn't the same size as Table Column ["+table.get(firstIsHeader ? 1 : 0).size()+"] Size");
		return size;
	}
	
	private static void calculateColumnWidth(List<List<String>> list, List<ColumnSettings> settings, boolean firstIsHeader, int size) {
		int startIndex = firstIsHeader ? 1 : 0;
		if(firstIsHeader) {
			ColumnSettings padding = settings.get(0);
			for(int i = 0,m=list.get(0).size();i<m;i++) {
				ColumnSettings setting = settings.get(i+startIndex);
				settings.set(i+startIndex, setting.calculateWidth(padding.left()+padding.right()+list.get(0).get(i).length()));
			}
		}
		for(int i = startIndex,m=list.size();i<m;i++) {
			List<String> column = list.get(i);
			for(int j = 0;j<column.size();j++) {
				ColumnSettings setting = settings.get(startIndex+j);
				String s = list.get(i).get(j);
				int length = s.length();
				if(setting.minRight() > 0) {
					String[] split = s.split(setting.minimumIndicator, 2);
					if(split.length != 2) split = new String[] {s, ""};
					length = split[0].length()+1+Math.max(split[1].length(), setting.minRight());
				}
				settings.set(startIndex+j, setting.calculateWidth(setting.left()+setting.right()+Math.max(list.get(i).get(j).length(), length)));
			}
		}
	}
	
	public record ColumnSettings(int left, int right, int minRight, String minimumIndicator, Alignment align, int width) {
		public ColumnSettings(int leftPadding, int rightPadding, Alignment alignment) {
			this(leftPadding, rightPadding, 0, "", alignment, -1);
		}
		
		public ColumnSettings calculateWidth(int newWidth) {
			return new ColumnSettings(left, right, minRight, minimumIndicator, align, Math.max(newWidth, width));
		}
		
		public String buildEmpty() {
			return " ".repeat(width);
		}
		
		public String buildSeperator() {
			return "-".repeat(width);
		}
		
		public int getAlignOffset(int length) {
			return getAlignOffset(length, align);
		}
		
		public int getAlignOffset(int length, Alignment align) {
			return switch(align) {
				case LEFT -> width - left;
				case CENTER -> (int)Math.round((width * 0.5F) + (length * 0.5F) + right - left);
				case RIGHT -> length + right;
				default -> left;
			};
		}
	}
	
	public static enum Alignment {
		LEFT,
		CENTER,
		RIGHT;
	}
}
