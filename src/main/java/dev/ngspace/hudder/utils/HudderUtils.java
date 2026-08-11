package dev.ngspace.hudder.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class HudderUtils {private HudderUtils() {}
	
	public static String[] processParemeters(String strtoprocess) {
		if (strtoprocess.isBlank()) return new String[0];
		
		int parentheses = 0;
		int squareparentheses = 0;

		StringBuilder parameterBuilder = new StringBuilder();
	    List<String> parameters = new ArrayList<String>();
		for (int i = 0;i<strtoprocess.length();i++) {
			char c = strtoprocess.charAt(i);
			if (c==','&&parentheses==0&&squareparentheses==0) {
				parameters.add(parameterBuilder.toString());
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
		parameters.add(parameterBuilder.toString());
		return parameters.toArray(String[]::new);
	}
	
	public static String checkIndentation(String text, int index) {
		StringBuilder b = new StringBuilder();
		for (;index<text.length();index++) {
			char c = text.charAt(index);
			if (!(c==' '||c=='\t')) break;
			b.append(c);
		}
		return b.toString();
	}
	
	public static long getCRC32Checksum(String str) {return getCRC32Checksum(str.getBytes());}
	public static long getCRC32Checksum(byte[] bytes) {
	    Checksum crc32 = new CRC32();
	    crc32.update(bytes, 0, bytes.length);
	    return crc32.getValue();
	}
}
