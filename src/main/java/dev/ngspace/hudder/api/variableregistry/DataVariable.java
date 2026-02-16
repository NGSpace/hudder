package dev.ngspace.hudder.api.variableregistry;

@FunctionalInterface
public interface DataVariable<T> {
	
	public default T getValue(String key) {
		return getValue0(key);
	}
	
	T getValue0(String key);
}
