package io.github.ngspace.hudder.v2runtime.values;

import java.util.Arrays;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.util.HudderUtils;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.V2Runtime;

public class V2ValueParser {private V2ValueParser() {}
	
	public static AV2Value of(V2Runtime runtime, String valuee, AV2Compiler compiler, int line, int charpos)
			throws CompileException {
		
		
		String value = valuee.trim();
		AV2Value temp = null;
		
		if (value.startsWith("(")&&value.endsWith(")"))
			return compiler.getV2Value(runtime, value.substring(1, value.length()-1), line, charpos);
		
		//Double constant
		if (value.matches("((0x|#)[\\daAbBcCdDeEfF]+|[-+]*\\d*(\\.?(\\d+)?))"))
			return new V2Number(value, line, charpos);

		if (value.equalsIgnoreCase("false")) return new V2Boolean(false, compiler, line, charpos);
		if (value.equalsIgnoreCase("true")) return new V2Boolean(true, compiler, line, charpos);
		
		
		//String constant
		if ((temp = string(value, compiler, line, charpos))!=null) return temp;
		
		//Array constant
		if (value.matches("\\[[\\s\\S]*\\]"))
			return new V2Array(HudderUtils.processParemeters(value.substring(1, value.length()-1).replace("\n", "")),
					compiler, runtime, line, charpos);
		
		//Set variable
		String[] setValues = value.split("=",2);
		if (setValues.length==2&&!compiler.isCondition(value)) 
			return new V2SetValue(compiler.getV2Value(runtime, setValues[0].toLowerCase(),line,charpos),
					compiler.getV2Value(runtime, setValues[1], line, charpos), compiler, line, charpos);
		
		
		boolean matchesVariableRegex = value.matches("[A-Za-z\\d][A-Za-z\\d_]*");
		
		//System variable
		if (matchesVariableRegex&&compiler.isSystemVariable(value.toLowerCase()))
			return new V2SystemVar(value, compiler, line, charpos);
		
		
		//Dynamic variable
		if (matchesVariableRegex) return new V2DynamicVar(value, compiler, line, charpos);
		
		
		//Temp dynamic variable
		if (value.matches("_[A-Za-z\\d_]*")) return new V2TempDynamicVar(value, compiler, line, charpos);
		
		
		//Read Array
		if (value.matches(".+ *\\[.+\\]"))
			return new V2ArrayRead(value, compiler, runtime, line, charpos);
		
		
		//Function variable
		if (!value.startsWith("(")&&value.endsWith(")")) {
			int argStart = value.indexOf("(");
			if (argStart!=-1) {
				String funcName = value.substring(0, argStart);
				if (funcName.matches("^[a-zA-Z0-9_.-]*$")) {
					String parametersString = value.substring(argStart+1, value.length()-1);
					String[] tokenizedArgs = HudderUtils.processParemeters(parametersString);
					
					return new V2FunctionVar(runtime, compiler, funcName, tokenizedArgs, line, charpos);
				}
			}
		}
		
		//Comparing values
		String operator = compiler.getOperator(value);
		if (operator!=null) {
			String[] v = value.split(operator,2);
			return new V2Comparison(compiler.getV2Value(runtime, v[0].trim(), line, charpos),
					compiler.getV2Value(runtime, v[1].trim(), line, charpos), operator, line, charpos);
		}
		
		
		//Math operation
		AV2Value[] values = new AV2Value[0];
		char c;
		StringBuilder mathvalue = new StringBuilder();
		char[] operations = new char[0];
		for (int i = 0;i<value.length();i++) {
			c = value.charAt(i);
			if (c=='"'&&mathvalue.isEmpty()) {
				boolean safe = false;
				i++;
				mathvalue.append(c);
				for (;i<value.length();i++) {
					c = value.charAt(i);
					if (c=='\\'&&!safe) safe = true; else {
						safe = false;
						mathvalue.append(c);
						if (c=='"'&&!safe) break;
					}
				}
				continue;
			}
			if (c=='('&&mathvalue.isEmpty()) {
				int parentheses = 0;
				i++;
				for (;i<value.length();i++) {
					c = value.charAt(i);
					if (c=='(') parentheses++;
					if (c==')') {parentheses--;if (parentheses==-1) break;}
					mathvalue.append(c);
				}
				continue;
			}
			if (c=='+'||c=='-'||c=='*'||c=='/'||c=='%') {
				values = addToArray(values, compiler.getV2Value(runtime, mathvalue.toString(), line, charpos));
				operations = addToArray(operations, c);
				mathvalue.setLength(0);
				continue;
			}
			mathvalue.append(c);
		}
		if (values.length>0) {
			values = addToArray(values, compiler.getV2Value(runtime, mathvalue.toString(), line, charpos));
			return new V2MathOperation(values,operations, line, charpos);
		}
		
		// Fallback
		throw new CompileException("Unknown variable: " + value, line, charpos);
	}
	
	private static V2String string(String value, AV2Compiler compiler, int line, int charpos) {
		//Maybe String :)
		if (!value.startsWith("\"")||!value.endsWith("\"")) return null;
		
		//Probably String :D
		value = value.substring(1,value.length()-1);
		StringBuilder string = new StringBuilder();
		char c;
		boolean safe = false;
		for (int i = 0;i<value.length();i++) {
			c = value.charAt(i);
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
}