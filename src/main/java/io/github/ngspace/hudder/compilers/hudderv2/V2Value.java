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
	
	public boolean isMath = false;
	public V2Value[] values;
	public char[] operations;
	
	protected V2Value(String value, AVarTextCompiler compiler) {
		super(value.toLowerCase(), compiler);
		isStatic = compiler.isStaticVariable(value.toLowerCase());
		if (isStatic) return;
		isDynamic = compiler.isDynamicVariable(value.toLowerCase());
		if (isDynamic) return;
		String[] conditionValues = value.split("=",2);
		isSet = conditionValues.length==2&&!compiler.isFirstEqualsCondition(value);
		if (isSet) {
			setKey = conditionValues[0];
			setValue = of(conditionValues[1], compiler);
			return;
		}
		char c;
		//TODO mathematical optimizations
		System.out.println(value.toLowerCase() + " " + isStatic + " " + isDynamic + " " + isSet);
	}
	
	//Should ideally be overwritten by anyone extending this class
	/**
	 * Process the value and return it as an Object.
	 * @return an Object of any kind.
	 * @throws CompileException
	 */
	public Object get() throws CompileException {
		if (isStatic) return compiler.getStaticVariable(value);
		if (isDynamic) return compiler.getStaticVariable(value);
		if (isSet) compiler.put(setKey, setValue.get());
		return compiler.getVariable(value);
	}
	
	public static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
	
	public static class V2StringValue extends V2Value {
		public V2StringValue(String value, AVarTextCompiler compiler) {
			this.value=value;
			this.compiler=compiler;
		}
		@Override public Object get() throws CompileException {
			return value;
		}
	}
	
	public static V2Value of(String valuee, AVarTextCompiler compiler) {
		String value = valuee.trim();
		if (!value.startsWith("\"")||!value.endsWith("\"")) return new V2Value(valuee, compiler);
		value = value.substring(1,value.length()-1);
		char c;
		boolean safe = false;
		for (int i = 0;i<value.length();i++) {
			c = value.charAt(i);
			if (c=='\\') safe = !safe; else {
				if (c=='"'&&!safe) return new V2Value(valuee, compiler); 
				safe = false;
			}
		}
		return new V2StringValue(value, compiler);
	}
}