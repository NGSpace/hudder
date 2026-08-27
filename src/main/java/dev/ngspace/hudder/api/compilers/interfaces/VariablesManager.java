package dev.ngspace.hudder.api.compilers.interfaces;

public interface VariablesManager {
	public void put(String key, Object value);
	public Object get(String key);
}
