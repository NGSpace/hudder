package io.github.ngspace.hudder.v2runtime.values;

import java.util.Arrays;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.V2Runtime;

public class V2ValueParser {private V2ValueParser() {}
	
	//Only after writing 80% of the values did I realize having one class that is all values is bad, I tried to
	//lower the burden but as you can see it's too late, the damage has already been done... maybe in a later update...
	public static AV2Value of(V2Runtime runtime, String valuee, AV2Compiler compiler) throws CompileException {
		String value = valuee.trim();
		AV2Value temp = null;
		
		
		//Double constant
		try {return new V2Number(Double.parseDouble(value), compiler);} catch (Exception e) {/*Do Nothin*/}
		

		if (value.equalsIgnoreCase("false")) return new V2Boolean(false, compiler);
		if (value.equalsIgnoreCase("true")) return new V2Boolean(true, compiler);
		
		
		//String constant
		if ((temp = string(value, compiler))!=null) return temp;
		
		
		//Set variable
		String[] setValues = value.split("=",2);
		if (setValues.length==2&&!compiler.isCondition(value)) 
			return new V2SetValue(setValues[0], compiler.getV2Value(runtime, setValues[1]), compiler);
		
		
		//System variable
		if (compiler.isSystemVariable(value.toLowerCase())) return new V2SystemVar(value.toLowerCase(), compiler);
		
		
		//Dynamic variable
		if (compiler.isDynamicVariable(value.toLowerCase())) return new V2DynamicVar(value.toLowerCase(), compiler);
		
		if (!value.startsWith("(")&&value.endsWith(")")) {
			int argStart = value.indexOf("(");
			if (argStart!=-1) {
				String funcName = value.substring(0, argStart);
				if (funcName.matches("^[a-zA-Z0-9_.-]*$")) {
					// TODO I notice I keep rewriting functions to tokenize args, I need to do smt bout it.
					// TODO uncomplicate this shit...
					String parametersString = value.substring(argStart+1, value.length()-1);
					String[] tokenizedArgs = new String[0];
					for (int i = 0;i<parametersString.length();i++) {
						char c = parametersString.charAt(i);
						if (c=='"') {
							StringBuilder stringParameter = new StringBuilder();
							boolean safe = false;
							for (;i<parametersString.length()&&c!='"'&&!safe;i++) {
								c = parametersString.charAt(i);
								if (!safe) {
									if (c=='\\') {safe = true;continue;}
								} else {
									if (c=='n') stringParameter.append('\n');
								}
								stringParameter.append(c);
							}
							//Make sure it is actually the end of the variable
							for (int j = i;j<parametersString.length();j++) {
								if (parametersString.charAt(j)!=' ') {
									if (parametersString.charAt(j)!=',') break;
									else throw new CompileException("Unable to parse parameters: " + parametersString);
								}
							}
							tokenizedArgs = addToArray(tokenizedArgs, stringParameter.toString());
						} else if (c==','||c==' ') {/**/} else {
//							Hudder.log(c + "r");
//							if (c=='"') throw new CompileException("Something fucked up");
							StringBuilder normalParameter = new StringBuilder();
							for (;i<parametersString.length()&&c!=',';) {
								c = parametersString.charAt(i);
								if (c==',') break;
								i++;
								normalParameter.append(c);
							}
							Hudder.log(normalParameter.toString());
	//						c = parametersString.charAt(i);
	//						Hudder.log(parametersString + "  " + i + "   " + c);
							tokenizedArgs = addToArray(tokenizedArgs, normalParameter.toString());
						}
					}

					Hudder.log(Arrays.toString(tokenizedArgs));
					Hudder.log(tokenizedArgs.length);
					
					return new V2FunctionVar(runtime, compiler, funcName, tokenizedArgs);
				}
			}
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
			if (c=='+'||c=='-'||c=='*'||c=='/'||c=='%'||c=='^') {
				values = addToArray(values, compiler.getV2Value(runtime, mathvalue.toString()));
				operations = addToArray(operations, c);
				mathvalue.setLength(0);
				continue;
			}
			mathvalue.append(c);
		}
		if (values.length>0) {
			values = addToArray(values, compiler.getV2Value(runtime, mathvalue.toString()));
			return new V2MathOperation(values,operations);
		}
		
		//Comparing values
		var operator = compiler.getOperator(value);
		var v = value.split(operator,2);
		return new V2Comparison(compiler.getV2Value(runtime, v[0].trim()), compiler.getV2Value(runtime, v[1].trim()), operator);
		
		// Fallback
	}
	
	private static V2String string(String value, AV2Compiler compiler) {
		//Maybe String :)
		if (!value.startsWith("\"")||!value.endsWith("\"")) return null;
		
		//Probably String :D
		value = value.substring(1,value.length()-1);
		StringBuilder string = new StringBuilder();
		char c;
		boolean safe = false;
		for (int i = 0;i<value.length();i++) {
			c = value.charAt(i);
			if (c=='\\'&&!safe) safe = true;
			else {
				if (c=='"'&&!safe) return null; //Not String ;_;
				safe = false;
				string.append(c);
			}
		}
		//String! :D
		return new V2String(string.toString(), compiler);
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