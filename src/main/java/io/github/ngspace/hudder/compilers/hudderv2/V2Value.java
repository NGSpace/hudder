package io.github.ngspace.hudder.compilers.hudderv2;

import java.util.Arrays;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.meta.MethodValue;

public class V2Value extends MethodValue {
	protected V2Value() {}
	public boolean isStatic = false;
	public boolean isDynamic = false;
	
	public boolean isSet = false;
	public String setKey;
	public V2Value setValue;
	public V2Value(String value, AVarTextCompiler compiler) {
		super(value.toLowerCase(), compiler);
		isStatic = compiler.isStaticVariable(value.toLowerCase());
		isDynamic = compiler.isDynamicVariable(value.toLowerCase());
		String[] values = value.split("=",2);
		isSet = values.length==2&&!compiler.isFirstEqualsCondition(value);
		if (isSet) {
			setKey = values[0];
			setValue = new V2Value(values[1], compiler);
			return;
		}
		int i = 0;
		//TODO mathematical optimizations
		System.out.println(value.toLowerCase() + " " + isStatic + " " + isDynamic + " " + isSet);
	}
	public Object toObject() throws CompileException {
		if (isStatic) return compiler.getStaticVariable(value);
		if (isDynamic) return compiler.getStaticVariable(value);
		if (isSet) compiler.put(setKey, setValue.toObject());
		return compiler.getVariable(value);
	}
	
	public static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
}