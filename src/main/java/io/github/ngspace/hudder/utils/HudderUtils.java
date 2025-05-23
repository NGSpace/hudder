package io.github.ngspace.hudder.utils;

public class HudderUtils {private HudderUtils() {}
	
	public static String[] processParemeters(String strtoprocess) {
		if (strtoprocess.isBlank()) return new String[0];
		
		int parentheses = 0;
		int squareparentheses = 0;

		StringBuilder parameterBuilder = new StringBuilder();
		String[] tokenizedParemeters = new String[0];
		for (int i = 0;i<strtoprocess.length();i++) {
			char c = strtoprocess.charAt(i);
			if (c==','&&parentheses==0&&squareparentheses==0) {
				tokenizedParemeters = addToArray(tokenizedParemeters, parameterBuilder.toString());
				parameterBuilder.setLength(0);
				continue;
			}
			if (c=='"') {
				parameterBuilder.append('"');
				i++;
				boolean safe = false;
				for (;i<strtoprocess.length();i++) {
					c = strtoprocess.charAt(i);
					if (!safe) {
						if (c=='\\') {safe = true;continue;}
					} else {
						if (c=='n') parameterBuilder.append('\n');
						else if (c=='"') parameterBuilder.append("\\\"");
						else if (c=='\\') parameterBuilder.append('\\');
						else parameterBuilder.append(c);
						safe = false;
						continue;
					}
					parameterBuilder.append(c);
					if (c=='"') {
						break;
					}
				}
				continue;
			}
			if (c=='(') parentheses++;
			if (c==')') parentheses--;
			if (c=='[') squareparentheses++;
			if (c==']') squareparentheses--;
			
			parameterBuilder.append(c);
		}
		tokenizedParemeters = addToArray(tokenizedParemeters, parameterBuilder.toString());
		return tokenizedParemeters;
	}
	private static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = java.util.Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
}
