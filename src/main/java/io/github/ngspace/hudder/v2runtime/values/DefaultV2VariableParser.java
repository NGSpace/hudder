package io.github.ngspace.hudder.v2runtime.values;

import java.util.Arrays;
import java.util.Objects;

import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.utils.HudderUtils;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.constants.V2Array;
import io.github.ngspace.hudder.v2runtime.values.constants.V2Boolean;
import io.github.ngspace.hudder.v2runtime.values.constants.V2Number;
import io.github.ngspace.hudder.v2runtime.values.constants.V2String;
import io.github.ngspace.hudder.v2runtime.values.modifiable.V2ArrayRead;
import io.github.ngspace.hudder.v2runtime.values.modifiable.V2DynamicVar;
import io.github.ngspace.hudder.v2runtime.values.modifiable.V2SetValue;
import io.github.ngspace.hudder.v2runtime.values.modifiable.V2TempDynamicVar;
import io.github.ngspace.hudder.v2runtime.values.operations.V2ClassPropertyCall;
import io.github.ngspace.hudder.v2runtime.values.operations.V2MathOperation;
import io.github.ngspace.hudder.v2runtime.values.operations.V2PostIncDecOperator;
import io.github.ngspace.hudder.v2runtime.values.operations.V2PreIncDecOperator;
import io.github.ngspace.hudder.v2runtime.values.operations.booloperations.V2Comparison;
import io.github.ngspace.hudder.v2runtime.values.operations.booloperations.V2LogicalAND;
import io.github.ngspace.hudder.v2runtime.values.operations.booloperations.V2LogicalOR;
import io.github.ngspace.hudder.v2runtime.values.operations.booloperations.V2OppositeOperator;

public class DefaultV2VariableParser implements IV2VariableParser {
	
	@Override public AV2Value parse(V2Runtime runtime, String valuee, AV2Compiler comp, int line, int charpos) throws CompileException {
		
		String value = valuee.trim();
		AV2Value[] values;// Some variables need it
		
		// Empty variable
		if (value.isBlank()) throw new CompileException("Unknown variable: empty variable", line, charpos);
		
		
		
		// Is wrapped in parenthesses? get rid of em!
		if (value.startsWith("(")&&value.endsWith(")")) {
			
			// Assume that it's good
			boolean isSafe = true;
			
			// Count how deep the parenthesses
			// NOTE it is initalized at 0 but since the first char is always going to be (, it will become one.
			int layers = 0;
			
			for (int i=0;i<value.length();i++) {
				char c = value.charAt(i);
				
				if (c=='(') layers++; // Layer up
				if (c==')') layers--; // Layer down
				
				if (layers==0) { // We reached the closing parenthesses of the first (.
					
					// Is there more chars to read? if so then the string is not wrapped and therefore shouldn't be unwrapped.
					isSafe = i+1==value.length();
					break;
				}
			}
			// if it is wrapped then remove the first and last chars to unwrap and reprocess them.
			if (isSafe) return comp.getV2Value(runtime, value.substring(1, value.length()-1), line, charpos);
		}
		
		
		
		// Double constant
		// Accepts the following formats: "0x(0-F)+", "#(0-F)+", "(0-9)+", "(0-9)*.(0-9)+"
		if (value.matches("((0x|#)[\\daAbBcCdDeEfF]+|[-+]*\\d*(\\.?(\\d+)?))"))
			return new V2Number(value, line, charpos, comp);
		
		
		
		// Boolean constants
		if (value.equalsIgnoreCase("false")) return new V2Boolean(false, comp, line, charpos, value);
		if (value.equalsIgnoreCase("true")) return new V2Boolean(true, comp, line, charpos, value);
		
		
		
		// String constant
		// I am too lazy to pull it out of the "string" method so it's stayin.
		AV2Value temp = string(value, comp, line, charpos);
		if (temp!=null) return temp;
		
		
		
		// Array constant
		// Accepts the follow format: "[(any char)]"
		if (value.matches("\\[[\\s\\S]*\\]")) {
			
			// Sends the text between the square brackets to HudderUtils.processParemeters to tokenize the values.
			
			return new V2Array(HudderUtils.processParemeters(value.substring(1, value.length()-1).replace("\n", "")),
					comp, runtime, line, charpos, value);
		}
		
		
		
		// Set variable
		String[] setValues = value.split("=",2);// Split at the first '='
		// Make sure it's not a condition!
		if (setValues.length==2&&!isCondition(value)) {
			return new V2SetValue(comp.getV2Value(runtime, setValues[0], line, charpos),
					comp.getV2Value(runtime, setValues[1], line, charpos), comp, line, charpos, value);
		}
		
		
		
		// Is it a variable name that does not start with _?
		boolean matchesVariableRegex = value.matches("[A-Za-z\\d][A-Za-z\\d_]*");
		
		// System variable
		if (matchesVariableRegex&&comp.isSystemVariable(value.toLowerCase())&&comp.SYSTEM_VARIABLES_ENABLED)
			return new V2SystemVar(value, comp, line, charpos);
		
		// Dynamic variable
		// It is not a systemvariable and is therefore a user defined/dynamic variable!
		if (matchesVariableRegex) return new V2DynamicVar(value, runtime, line, charpos);
		
		
		
		// Temp dynamic variable
		// Is it a variable name that starts with _?
		if (value.matches("_[A-Za-z\\d_]*")) return new V2TempDynamicVar(value, comp, line, charpos);
		
		
		
		// Read Array
		// Accepts the following format: "(any char)+(space)?[(any char)]".
		if (value.matches(".+ *\\[.+\\]"))
			return new V2ArrayRead(value, comp, runtime, line, charpos, value);
		
		
		// Function variable
		if (!value.startsWith("(")&&value.endsWith(")")) {
			// Same thing as before except we start reading at the first instance of a '(' char instead of at index 0.
			int argStart = value.indexOf("(");
			boolean isSafe = false;
			int parenthesses = 0;
			if (argStart!=-1) {
				isSafe = true;
				for (int i = argStart;i<value.length();i++) {
					char c = value.charAt(i);
					if (c=='(')
						parenthesses++;
					if (c==')')
						parenthesses--;
					if (parenthesses==0) {
						isSafe = i+1==value.length();
						break;
					}
				}
				if (isSafe) {
					String funcName = value.substring(0, argStart);
					if (funcName.matches("^[a-zA-Z0-9_-]*$")) {
						String parametersString = value.substring(argStart+1, value.length()-1);
						String[] tokenizedArgs = HudderUtils.processParemeters(parametersString);
						
						return new V2FunctionVar(runtime, comp, funcName, tokenizedArgs, line, charpos, value);
					}
				}
			}
		}
		
		
		
		//Logical OR operator
		values = logicalOperator('|', value, runtime, line, charpos);
		if (values.length>1)
			return new V2LogicalOR(values, line, charpos, value, comp);
		
		
		
		//Logical AND operator
		values = logicalOperator('&', value, runtime, line, charpos);
		if (values.length>1)
			return new V2LogicalAND(values, line, charpos, value, comp);
		

		//Comparing values
		String operator = getOperator(value);
		if (operator!=null) {
			int parenthesses = 0;
			String[] v = value.split(operator,2);
			for (char c : v[0].trim().toCharArray()) {
				if (c=='(') parenthesses++;
				if (c==')') parenthesses--;
			}
			if (parenthesses==0)
				return new V2Comparison(comp.getV2Value(runtime, v[0].trim(), line, charpos),
					comp.getV2Value(runtime, v[1].trim(), line, charpos), operator, line, charpos, value, comp);
		}

		
		// Post Increase and Decrease Operator
		if (value.matches("[\\s\\S]+(\\+\\+|--)")) {
			return new V2PostIncDecOperator(comp.getV2Value(runtime, value.substring(0,value.length()-2),
					line, charpos), comp, line, charpos, "+".equals(value.substring(value.length()-1)), value);
		}

		// Pre Increase and Decrease Operator
		if (value.matches("(\\+\\+|--)[\\s\\S]+")) {
			return new V2PreIncDecOperator(comp.getV2Value(runtime, value.substring(2),
					line, charpos), comp, line, charpos, "+".equals(value.substring(0, 1)), value);
		}
		
		
		//Math operation
		values = new AV2Value[0];
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
					values = new AV2Value[0];
					break;
				}
				values = addToArray(values, comp.getV2Value(runtime, mathvalue.toString(), line, charpos));
				operations = addToArray(operations, c);
				mathvalue.setLength(0);
				continue;
			}
			mathvalue.append(c);
		}
		if (values.length>0) {
			values = addToArray(values, comp.getV2Value(runtime, mathvalue.toString(), line, charpos));
			return new V2MathOperation(values, operations, line, charpos, value, comp);
		}



		// Class
		String classyobjname = "";
		String functionOrObject = "";
		for (int i=1;i<value.length(); i++) {
			char c = value.charAt(value.length()-i);
			if (c==')') {
				int parentheses = 0;
				for (;i<value.length()+1; i++) {
					c = value.charAt(value.length()-i);
					if (c==')') parentheses++;
					if (c=='(') parentheses--;
					functionOrObject = c + functionOrObject;
					if (parentheses==0) break;
				}
				continue;
			}

			if (c=='"') {
				boolean isnotescaped = false;
				for (;i<value.length()+1; i++) {
					c = value.charAt(value.length()-i);
					functionOrObject = c + functionOrObject;
					if (i+2<value.length()+1) isnotescaped = value.charAt(value.length()-i) == '\\';
					if (c=='"'&&!(i+1<value.length()+1&&value.charAt(value.length()-i)=='\\')&&isnotescaped) break;
				}
				continue;
			}

			if (c=='.') {
				classyobjname = value.substring(0,value.length()-i);
				for (int j=1;j<classyobjname.length(); j++) {
					char cc = classyobjname.charAt(classyobjname.length()-j);
					if (Character.isDigit(cc)) continue;
					if (cc=='*'||cc=='+'||cc=='-'||cc=='/'||cc=='%') {
						classyobjname = "";
						functionOrObject = "";
						break;
					} else {
						break;
					}
				}
				break;
			}
			functionOrObject = c + functionOrObject;
		}

		if (!Objects.equals(functionOrObject, value)&&!"".equals(classyobjname)) {
			return new V2ClassPropertyCall(charpos, charpos, value, comp, runtime,
					comp.getV2Value(runtime, classyobjname, line, charpos), functionOrObject);
		}
		
		
		
		
		// ! Operator
		if (value.matches("![\\s\\S]+"))
			return new V2OppositeOperator(comp.getV2Value(runtime, value.substring(1), line, charpos),
					line, charpos, value, comp);
		
		
		
		
		// Fallback
		throw new CompileException("Untokenizable variable: " + value, line, charpos);
	}
	
	private V2String string(String value, AV2Compiler compiler, int line, int charpos) {
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
		return new V2String(string.toString(), compiler, line, charpos);
	}
	
	
	
	private AV2Value[] logicalOperator(char op, String value, V2Runtime runtime, int line, int charpos) throws CompileException {
		AV2Value[] values = new AV2Value[0];
		StringBuilder builder = new StringBuilder();
		for (int i = 0;i<value.length();i++) {
			char c = value.charAt(i);
			if (c=='"'&&builder.isEmpty()) {
				boolean safe = false;
				i++;
				builder.append(c);
				for (;i<value.length();i++) {
					c = value.charAt(i);
					if (c=='\\'&&!safe) safe = true; else {
						safe = false;
						builder.append(c);
						if (c=='"'&&!safe) break;
					}
				}
				continue;
			}
			if (c=='('&&builder.isEmpty()) {
				int parentheses = 1;
				i++;
				for (;i<value.length();i++) {
					c = value.charAt(i);
					if (c=='(') parentheses++;
					if (c==')') parentheses--;
					if (parentheses==0) break;
					builder.append(c);
				}
				continue;
			}
			if (c==op&&i+1<value.length()&&value.charAt(i+1)==op) {
				i++;
				values = addToArray(values, runtime.compiler.getV2Value(runtime, builder.toString(), line, charpos));
				builder.setLength(0);
				continue;
			}
			
			builder.append(c);
		}
		if (!Objects.equals(value, builder.toString()))
			return addToArray(values, runtime.compiler.getV2Value(runtime, builder.toString(), line, charpos));
		else return values;
	}
	
	
	
	private <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
	private char[] addToArray(char[] arr, char t) {
		char[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
	

	private String getOperator(String condString) {
		if (condString.contains("==")) return "==";
		if (condString.contains("!=")) return "!=";
		if (condString.contains(">=")) return ">=";
		if (condString.contains("<=")) return "<=";
		if (condString.contains(">" )) return ">" ;
		if (condString.contains("<" )) return "<" ;
		return null;
	}
	
	private boolean isCondition(String key) {
		int i = key.indexOf('=');
		if (i==-1&&!key.contains(">")&&!key.contains("<")) return false;
		if (i==key.length()) return false;
		if (i==0) return false;
		char pre = key.charAt(i-1);
		return pre=='<'||pre=='>'||pre=='!'||key.charAt(i+1)=='=';
	}
}
