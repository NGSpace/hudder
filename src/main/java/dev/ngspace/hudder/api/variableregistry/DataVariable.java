package dev.ngspace.hudder.api.variableregistry;

/**
 * Represents a variable whose value is resolved using a supplied key.
 *
 * @param <T> the type of value returned by this variable
 */
@FunctionalInterface
public interface DataVariable<T> {
	
	/**
	 * Returns the value associated with the specified key.
	 * <p>
	 * This method delegates value resolution to {@link #getValue0(String)}.
	 * </p>
	 *
	 * @param key the key used to resolve the value
	 * @return the value resolved for the specified key
	 */
	public default T getValue(String key) {
		return getValue0(key);
	}
	
	/**
	 * Resolves the value associated with the specified key.
	 *
	 * @param key the key used to resolve the value
	 * @return the value resolved for the specified key
	 */
	T getValue0(String key);
	
}