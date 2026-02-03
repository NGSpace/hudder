package io.github.ngspace.hudder.data_management.api;

public class VariableTypes {
	
	private VariableTypes() {}

	public static final Type<Boolean> BOOLEAN = new Type<Boolean>(Boolean.class);
	public static final Type<String> STRING = new Type<String>(String.class);
	public static final Type<Double> NUMBER = new Type<Double>(Double.class);
	public static final Type<Object> OBJECT = new Type<Object>(Object.class);
	
	public static class Type<T> {
		private Class<T> clazz;
		private Type(Class<T> clazz) {
			this.clazz = clazz;
		}
		public Class<T> getTypeClass() {
			return clazz;
		}
	}
}
