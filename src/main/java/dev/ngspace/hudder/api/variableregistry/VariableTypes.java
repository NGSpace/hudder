package dev.ngspace.hudder.api.variableregistry;

/**
 * Defines the variable types supported by the data variable registry.
 * <p>
 * Each type contains the Java class associated with values registered under
 * that type.
 * </p>
 *
 * @see DataVariableRegistry
 * @deprecated No longer needed by the registry.
 */
@Deprecated(since = "10.3.0", forRemoval = false)
public class VariableTypes {
	
	private VariableTypes() {}

	/**
	 * The variable type used for {@link Boolean} values.
	 */
	@Deprecated(since = "10.3.0", forRemoval = false)
	public static final Type<Boolean> BOOLEAN = new Type<Boolean>(Boolean.class);

	/**
	 * The variable type used for {@link String} values.
	 */
	@Deprecated(since = "10.3.0", forRemoval = false)
	public static final Type<String> STRING = new Type<String>(String.class);

	/**
	 * The variable type used for numeric values represented by {@link Double}.
	 */
	@Deprecated(since = "10.3.0", forRemoval = false)
	public static final Type<Double> NUMBER = new Type<Double>(Double.class);

	/**
	 * The variable type used for general {@link Object} values.
	 */
	@Deprecated(since = "10.3.0", forRemoval = false)
	public static final Type<Object> OBJECT = new Type<Object>(Object.class);
	
	/**
	 * Represents a supported variable type and its associated Java class.
	 *
	 * @param <T> the value type represented by this instance
	 */
	@Deprecated(since = "10.3.0", forRemoval = false)
	public static class Type<T> {
		private Class<T> clazz;
		private Type(Class<T> clazz) {
			this.clazz = clazz;
		}
		@Deprecated(since = "10.3.0", forRemoval = false)
		public Class<T> getTypeClass() {
			return clazz;
		}
	}
}
