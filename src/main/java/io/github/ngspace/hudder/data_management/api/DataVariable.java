package io.github.ngspace.hudder.data_management.api;

import java.util.function.Function;

@FunctionalInterface
public interface DataVariable<T> extends Function<String, T> {
	
	@Override
	default T apply(String key) {
		return getValue(key);
	}
	
	public T getValue(String key);
}
