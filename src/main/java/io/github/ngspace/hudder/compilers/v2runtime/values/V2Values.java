package io.github.ngspace.hudder.compilers.v2runtime.values;

import io.github.ngspace.hudder.compilers.v2runtime.AV2Compiler;

public class V2Values {private V2Values() {}
	//Only after writing 80% of the values did I realize having one class that is all values is bad, I tried to
	//lower the burden but as you can see it's too late, the damage has already been done... maybe in a later update...
	public static V2Value of(String valuee, AV2Compiler compiler) {
		String value = valuee.trim();
		V2Value temp = null;
		
		//Maybe Double :3
		try {return new V2Number(Double.parseDouble(value), compiler);} catch (Exception e) {/*Do Nothin*/}
		
		if ((temp = string(value, compiler))!=null) return temp;
		
		if (compiler.isStaticVariable(value.toLowerCase())) return new V2StaticVar(value.toLowerCase(), compiler);
		
		if (compiler.isDynamicVariable(value.toLowerCase())) return new V2DynamicVar(value.toLowerCase(), compiler);
		
		return new V2Value(valuee, compiler);
	}

	//TODO Fix setting a value to a string will make the String lowercase, I've got bigger issues rn.
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
			if (c=='\\'&&!safe) safe = true; else {
				if (c=='"'&&!safe) return null; //Not String ;_;
				safe = false;
				string.append(c);
			}
		}
		//String! :D
		return new V2String(string.toString(), compiler);
	}
}