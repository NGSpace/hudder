package dev.ngspace.hudder.api.compilers.interfaces;

public interface VariablesManager extends VariablesProvider {
	public void putVariable(String key, Object value);
}
