package dev.ngspace.hudder.hudderv3;

import java.util.Arrays;

import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.exceptions.ExecutionException;

public class V3VariableProcessor {

	public void parseVariable(V3MethodWriter methodWriter, String valuee, HudderV3Compiler comp) throws ExecutionException {

		String value = valuee.trim();
		
		// Empty variable
		if (value.isBlank()) throw new ExecutionException("Unknown variable: empty variable", -1, -1);
		
		
		
		// Is wrapped in parenthesses? get rid of em!
		if (value.startsWith("(")&&value.endsWith(")")) {
			
			// Assume that it's good
			boolean isSafe = true;
			
			boolean quotes = false;
			boolean backslash = false;
			
			// Count how deep the parenthesses
			// NOTE it is initalized at 0 but since the first char is always going to be (, it will become one.
			int layers = 0;
			
			for (int i=0;i<value.length();i++) {
				char c = value.charAt(i);
				
				if (quotes) {
					if (!backslash) {
						if (c=='"') quotes = false;
						if (c=='\\') {backslash = !backslash;continue;}
					}
					backslash = false;
					continue;
				}

				if (c=='"') quotes = true;
				if (c=='(') layers++; // Layer up
				if (c==')') layers--; // Layer down
				
				if (layers==0) { // We reached the closing parenthesses of the first (.
					
					// Is there more chars to read? if so then the string is not wrapped and therefore shouldn't be unwrapped.
					isSafe = i+1==value.length();
					break;
				}
			}
			// if it is wrapped then remove the first and last chars to unwrap and reprocess them.
			if (isSafe) {parseVariable(methodWriter, value.substring(1, value.length()-1), comp);}
		}
		
		
		
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
				int valueindex = methodWriter.astore();
				methodWriter.aload(0);
				methodWriter.loadConstant(setValues[0]);
				methodWriter.aload(valueindex);
				methodWriter.call(AVarTextCompiler.class, "put", "(Ljava/lang/String;Ljava/lang/Object;)V",
						false);
				methodWriter.aload(valueindex);
				return;
			}
		}
		

		
		// Is it a variable name that does not start with _?
		boolean matchesVariableRegex = value.matches("[A-Za-z\\d][A-Za-z\\d_]*");
		
		// System variable
		if (matchesVariableRegex&&comp.isSystemVariable(value.toLowerCase())) {
			methodWriter.callDataVariableRegistry(value.toLowerCase());
			return;
		}
		
		// Dynamic variable
		if (matchesVariableRegex) {
			methodWriter.aload(0);
			methodWriter.loadConstant(value.toLowerCase());
			methodWriter.call(AVarTextCompiler.class, "get", "(Ljava/lang/String;)Ljava/lang/Object;", false);
			int index = methodWriter.astore();
			methodWriter.aload(index);
			return;
		}

		//Comparing values
		String operator = getOperator(value);
		if (operator!=null) {
			int parenthesses = 0;
			boolean quotes = false;
			boolean backslash = false;
			String[] v = value.split(operator,2);
			for (char c : v[0].trim().toCharArray()) {
				if (quotes) {
					if (!backslash) {
						if (c=='"')
							quotes = false;
						if (c=='\\')
							backslash = true;
					}
				} else {
					if (c=='"') quotes = true;
					if (c=='(') parenthesses++;
					if (c==')') parenthesses--;
				}
			}
			if (parenthesses==0) {
				parseVariable(methodWriter, v[0].trim(), comp);
				int val1index = methodWriter.astore();
				parseVariable(methodWriter, v[1].trim(), comp);
				int val2index = methodWriter.astore();
				methodWriter.aload(val1index);
				methodWriter.aload(val2index);
				methodWriter.loadConstant(operator);
				methodWriter.callStatic(HudderV3Helper.class, "compare",
						"(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)Z", false);
			    methodWriter.callStatic(
						Boolean.class,
						"valueOf",
						"(Z)Ljava/lang/Boolean;",
						false
					);
				return;
			}
		}
			
			
		//Math operation
		String[] values = new String[0];
		StringBuilder mathvalue = new StringBuilder();
		char[] operations = new char[0];
		for (int i = 0;i<value.length();i++) {
			char c = value.charAt(i);
			if (c=='"'&&mathvalue.isEmpty()) {
				boolean safe = false;
				i++;
				mathvalue.append(c);
				for (;i<value.length();i++) {
					c = value.charAt(i);
					if (c=='\\'&&!safe) {safe = true;mathvalue.append(c);} else {
						safe = false;
						mathvalue.append(c);
						if (c=='"'&&!safe) break;
					}
				}
				continue;
			}
			if (c=='(') {
				int parentheses = 1;
				mathvalue.append(c);
				i++;
				for (;i<value.length();i++) {
					c = value.charAt(i);
					if (c=='(') parentheses++;
					if (c==')') {
						parentheses--;
						if (parentheses==0) {
							mathvalue.append(c);
							break;
						}
					}
					mathvalue.append(c);
				}
				continue;
			}
			if (c=='+'||c=='-'||c=='*'||c=='/'||c=='%') {
				if (mathvalue.toString().isBlank()&&c=='-') {
					mathvalue.append(c);
					continue;
				}
				if (mathvalue.toString().isBlank()) {//Do not trigger
					values = new String[0];
					break;
				}
				values = addToArray(values, mathvalue.toString());
				operations = addToArray(operations, c);
				mathvalue.setLength(0);
				continue;
			}
			mathvalue.append(c);
		}
		if (values.length>0) {
			values = addToArray(values, mathvalue.toString());
			methodWriter.complexMath(this, comp, values, operations);
			return;
//			return new V2MathOperation(values, operations, line, charpos, value, comp);
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
