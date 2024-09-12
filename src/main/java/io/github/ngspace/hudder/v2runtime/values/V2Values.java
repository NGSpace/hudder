package io.github.ngspace.hudder.v2runtime.values;

import java.util.Arrays;

import io.github.ngspace.hudder.v2runtime.AV2Compiler;

public class V2Values {private V2Values() {}
	//Only after writing 80% of the values did I realize having one class that is all values is bad, I tried to
	//lower the burden but as you can see it's too late, the damage has already been done... maybe in a later update...
	public static V2Value of(String valuee, AV2Compiler compiler) {
		String value = valuee.trim();
		V2Value temp = null;
		
		
		//Double constant
		try {return new V2Number(Double.parseDouble(value), compiler);} catch (Exception e) {/*Do Nothin*/}
		
		
		//String constant
		if ((temp = string(value, compiler))!=null) return temp;
		
		
		//System variable
		if (compiler.isSystemVariable(value.toLowerCase())) return new V2SystemVar(value.toLowerCase(), compiler);
		
		
		//Dynamic variable
		if (compiler.isDynamicVariable(value.toLowerCase())) return new V2DynamicVar(value.toLowerCase(), compiler);
		
		
		//Set variable
		String[] setValues = value.split("=",2);
		if (setValues.length==2&&!compiler.isCondition(value)) 
			return new V2SetValue(setValues[0], compiler.getV2Value(setValues[1]), compiler);
		
		
		//Math operation
		V2Value[] values = new V2Value[0];
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
				values = addToArray(values, compiler.getV2Value(mathvalue.toString()));
				operations = addToArray(operations, c);
				mathvalue.setLength(0);
				continue;
			}
			mathvalue.append(c);
		}
		if (values.length>0) {
			values = addToArray(values, compiler.getV2Value(mathvalue.toString()));
			return new V2MathOperation(values,operations);
		}
		
		//Comparing values
		var operator = compiler.getOperator(value);
		var v = value.split(operator,2);
		return new V2Comparison(compiler.getV2Value(v[0]), compiler.getV2Value(v[1]), operator);
		
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