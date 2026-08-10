package dev.ngspace.hudder.testing;

import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.utils.HudderUtils;

public class HudderTestReader {
	
	public static Result process(String test) {
		
		test = test.replace("\r", "");
		
		Map<String, String[]> metadata = new HashMap<String, String[]>();
		String input = "";
		String output = "";
		
		int index = 0;
		boolean end_for = false;
		
		for (; index < test.length(); index++) {
			if (end_for) break;
			char c = test.charAt(index);
			if (Character.isWhitespace(c)) continue;
			switch (c) {
				case '@': {
					String line = getLine(index+1, test);
					String[] nameandvalue = line.split("=", 2);
					metadata.put(nameandvalue[0], HudderUtils.processParemeters(nameandvalue[1]));
					index += line.length();
					break;
				}
				case '#': {
					index += getLine(index+1, test).length();
					break;
				}
				case 'i', 'o': {
					String line = getLine(index, test);
					String end_str = "\nend_"+(c=='i'?"in":"out")+"put;";
					if (!((c=='i'?"in":"out")+"put:").equals(line))
						throw new IllegalArgumentException("Unrecognized: " + line);
					index += line.length()+1;
					int end = test.indexOf(end_str, index);
					String indentation = HudderUtils.checkIndentation(test, index);
					if (!indentation.startsWith("\t")) {
						throw new IllegalArgumentException("Test input and output must be indented with a tab!");
					}
					if (c=='i')
						input = removeIndentation("\t", test.substring(index, end));
					else
						output = removeIndentation("\t", test.substring(index+1, end));
					index = end+end_str.length();
					
					break;
				}
				case 'E': {
					String line = getLine(index, test);
					if (!"END_TEST;".equals(line))
						throw new IllegalArgumentException("Unrecognized: " + line);
					index += line.length();
					end_for = true;
					break;
				}
				default:
					throw new IllegalArgumentException("Unrecognized: " + getLine(index, test));
			}
		}
		
		if (index<test.length()) {
			String trailing = test.substring(index);
			if (!trailing.isBlank())
				throw new IllegalArgumentException("Unrecognized: " + trailing);
		}
		
		return new Result(metadata, input, output);
	}
	
	public static String getLine(int index, String test) {
		int ind = test.indexOf('\n', index);
		return test.substring(index, ind==-1?test.length():ind);
	}
	
	public static String removeIndentation(String indentation, String text) {
		if (indentation.isEmpty())
			return text;
		
		StringBuilder result = new StringBuilder(text.length());
		boolean startOfLine = true;
		
		for (int index = 0; index < text.length();) {
			
			if (startOfLine && text.startsWith(indentation, index))
				index += indentation.length();
				
			if (index >= text.length())
				break;
			
			char c = text.charAt(index++);
			result.append(c);
			
			startOfLine = c == '\n' || c == '\r';
		}
		
		return result.toString();
	}
	
	public static record Result(Map<String, String[]> metadata, String input, String output) {
		public String getString(String key) {
			return metadata().get(key)[0];
		}
		
		public String[] get(String key) {
			return metadata().get(key);
		}
	}
}
