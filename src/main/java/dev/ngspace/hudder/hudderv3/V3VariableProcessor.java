package dev.ngspace.hudder.hudderv3;

import java.util.Arrays;
import java.util.Map;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.values.modifiable.V2SetValue;

public class V3VariableProcessor {
	private V3VariableProcessor() {}

	public static void parseVariable(V3MethodWriter methodWriter, String valuee, HudderV3Compiler comp) throws ExecutionException {

		String value = valuee.trim();
		
		
		
		// Double constant
		// Accepts the following formats: "0x(0-F)+", "#(0-F)+", "(0-9)+", "(0-9)*.(0-9)+"
		if (value.matches("((0x|#)[\\daAbBcCdDeEfF]+|[-+]?\\d*(\\.?(\\d+)?))")) {
			if (value.startsWith("0x")) {
				methodWriter.loadConstant(Integer.parseUnsignedInt(value.substring(2), 16));
				return;
			} else if (value.startsWith("#")) {
				methodWriter.loadConstant(Integer.parseUnsignedInt(value.substring(1), 16));
				return;
			} else {
				methodWriter.loadConstant(Double.parseDouble(value));
				return;
			}
		}
		
		
		
		// Boolean constants
		if (value.equalsIgnoreCase("false")) {methodWriter.loadConstant(false);return;}
		if (value.equalsIgnoreCase("true")) {methodWriter.loadConstant(true);return;}
		
		
		
		// String constant
		String temp = string(value);
		if (temp!=null) {
			methodWriter.loadConstant(temp);
			return;
		}
		
		
		
		// Set variable
		String[] setValues = value.split("=",2);// Split at the first '='
		// Make sure it's not a condition!
		if (setValues.length==2&&!isCondition(value)) {
			
			boolean valid = true;
			
			boolean escaped = false;
			for (int i = 0;i<setValues[0].length();i++) {
				char c = setValues[0].charAt(i);
				if (c=='\\') {
					escaped = true;
				}
				if (escaped) continue;
				if (c=='"') {
					valid = false;
					break;
				}
			}
			if (valid) {
				parseVariable(methodWriter, setValues[1], comp);
				int valueindex = methodWriter.store();
				methodWriter.pop();
				methodWriter.aload(0);
				methodWriter.loadConstant(setValues[0]);
				methodWriter.aload(valueindex);
				methodWriter.call(AVarTextCompiler.class, "put", "(Ljava/lang/String;Ljava/lang/Object;)V",
						false);
				methodWriter.pop();
				methodWriter.aload(valueindex);
//				methodWriter.getStatic(AVarTextCompiler.class, "variables", Map.class);
				//methodWriter.loadConstant(setValues[0]);
				
			}
//				return new V2SetValue(comp.getV2Value(runtime, setValues[0], line, charpos),
//					comp.getV2Value(runtime, setValues[1], line, charpos), comp, line, charpos, value);
		}
		

		
		// Is it a variable name that does not start with _?
		boolean matchesVariableRegex = value.matches("[A-Za-z\\d][A-Za-z\\d_]*");
		
		// System variable
		if (matchesVariableRegex&&comp.isSystemVariable(value.toLowerCase())) {
			methodWriter.callDataVariableRegistry(value.toLowerCase());
			return;
		}
		
		// Dynamic variable
		if (matchesVariableRegex&&comp.isSystemVariable(value.toLowerCase())) {
//			methodWriter.callDataVariableRegistry(value.toLowerCase());
//			return;
			throw new ExecutionException("Untokenizable variable: " + value, -1, -1);
		}

		throw new ExecutionException("Untokenizable variable: " + value, -1, -1);
	}
	
	private static String string(String value) {
		//Maybe String :)
		if (!value.startsWith("\"")||!value.endsWith("\"")) return null;
		
		//Probably String :D
		value = value.substring(1,value.length()-1);
		StringBuilder string = new StringBuilder();
		
		boolean safe = false;
		for (int i = 0;i<value.length();i++) {
			char c = value.charAt(i);
			if (c=='n'&&safe) {
				string.append('\n');
				continue;
			}
			if (c=='\\'&&!safe) safe = true;
			else {
				if (c=='"'&&!safe) return null; //Not String ;_;
				safe = false;
				string.append(c);
			}
		}
		//String! :D
		return string.toString();
	}
	
	
	
	private static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
	private static char[] addToArray(char[] arr, char t) {
		char[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
	

	private static String getOperator(String condString) {
		if (condString.contains("==")) return "==";
		if (condString.contains("!=")) return "!=";
		if (condString.contains(">=")) return ">=";
		if (condString.contains("<=")) return "<=";
		if (condString.contains(">" )) return ">" ;
		if (condString.contains("<" )) return "<" ;
		return null;
	}
	
	private static boolean isCondition(String key) {
		int i = key.indexOf('=');
		if (i==-1&&!key.contains(">")&&!key.contains("<")) return false;
		if (i==key.length()) return false;
		if (i==0) return false;
		char pre = key.charAt(i-1);
		return pre=='<'||pre=='>'||pre=='!'||key.charAt(i+1)=='=';
	}
}
