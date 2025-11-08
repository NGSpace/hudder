package io.github.ngspace.hudder.data_management.api;

@FunctionalInterface
public interface DataVariable<T> {
	
	public default T getValue(String key) {
		return getValue0(key);
	}
	
	T getValue0(String key);
}
