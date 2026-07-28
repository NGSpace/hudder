package dev.ngspace.hudder.hudderv3;

import java.util.Arrays;

import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.instructions.variables.FunctionCallVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.SystemVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.ArrayConstantVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.BooleanVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.NumberVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.constants.StringVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.modifiable.DynamicVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.modifiable.SetVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.MathVariableVisitor;
import dev.ngspace.hudder.hudderv3.instructions.variables.operations.booloperations.ComparisionVariableVisitor;
import dev.ngspace.hudder.utils.HudderUtils;
import dev.ngspace.hudder.v2runtime.values.constants.V2Array;

public class V3VariableProcessor {

	public VariableVisitor parseVariable(String valuee, AV3Compiler comp) throws ExecutionException {

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
			if (isSafe) {
				return parseVariable(value.substring(1, value.length()-1), comp);
			}
		}
		
		
		
		// Double constant
		// Accepts the following formats: "0x(0-F)+", "#(0-F)+", "(0-9)+", "(0-9)*.(0-9)+"
		if (value.matches("((0x|#)[\\daAbBcCdDeEfF]+|[-+]?\\d*(\\.?(\\d+)?))")) {
			return new NumberVariableVisitor(comp, value);
		}
		
		
		
		// Boolean constants
		if (value.equalsIgnoreCase("false")) {return new BooleanVariableVisitor(comp,false);}
		if (value.equalsIgnoreCase("true")) {return new BooleanVariableVisitor(comp,true);}
		
		
		
		// String constant
		String temp = string(value);
		if (temp!=null) {
			return new StringVariableVisitor(comp, temp);
		}
		
		
		
		// Array constant
		// Accepts the follow format: "[(any char)]"
		if (value.matches("\\[[\\s\\S]*\\]")) {
			
			// Sends the text between the square brackets to HudderUtils.processParemeters to tokenize the values.
			
			return new ArrayConstantVariableVisitor(HudderUtils.processParemeters(value.substring(1, value.length()-1).replace("\n", "")),
					comp);
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
				return new SetVariableVisitor(comp, setValues[0], setValues[1]);
			}
		}
		

		
		// Is it a variable name that does not start with _?
		boolean matchesVariableRegex = value.matches("[A-Za-z\\d][A-Za-z\\d_]*");
		
		// System variable
		if (matchesVariableRegex&&comp.isSystemVariable(value.toLowerCase())) {
			return new SystemVariableVisitor(comp, value.toLowerCase());
		}
		
		// Dynamic variable
		if (matchesVariableRegex) {
			return new DynamicVariableVisitor(comp, value.toLowerCase());
		}
		
		
		// Function variable
		if (!value.startsWith("(")&&value.endsWith(")")) {
			// Same thing as before except we start reading at the first instance of a '(' char instead of at index 0.
			int argStart = value.indexOf("(");
			boolean isSafe = false;
			boolean quotes = false;
//			boolean backslash = false;
			int parenthesses = 0;
			if (argStart!=-1) {
				isSafe = true;
				for (int i = argStart;i<value.length();i++) {
					char c = value.charAt(i);
					if (c=='"') {
						quotes = !quotes;
					}
					if (quotes) {
						continue;
					}
					if (c=='(') parenthesses++;
					if (c==')') parenthesses--;
					if (parenthesses==0) {
						isSafe = i+1==value.length();
						break;
					}
				}
				if (isSafe) {
					String funcName = value.substring(0, argStart);
					if (funcName.matches("^[a-zA-Z0-9]+[a-zA-Z0-9_-]*$")) {
						String parametersString = value.substring(argStart+1, value.length()-1);
						return new FunctionCallVariableVisitor(funcName, comp,
								HudderUtils.processParemeters(parametersString));
					}
				}
			}
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
				return new ComparisionVariableVisitor(comp, v[0], v[1], operator);
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
			return new MathVariableVisitor(values, operations, comp);
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
