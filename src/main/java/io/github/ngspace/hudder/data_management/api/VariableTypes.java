package io.github.ngspace.hudder.data_management.api;

public class VariableTypes {
	
	private VariableTypes() {}

	public static final Type<Boolean> BOOLEAN = new Type<Boolean>();
	public static final Type<String> STRING = new Type<String>();
	public static final Type<Double> NUMBER = new Type<Double>();
	public static final Type<Object> OBJECT = new Type<Object>();
	
	public static class Type<T> {
		private Type() {}
	}
}
