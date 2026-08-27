package dev.ngspace.hudder.api.compilers.interfaces;

import dev.ngspace.hudder.exceptions.ExecutionException;

public interface VariablesProvider {
	/**
	 * Retrieves a variable using the specified key.
	 *
	 * @param key the key of the variable to retrieve
	 * @return the value associated with the specified key
	 * @throws ExecutionException if the variable cannot be retrieved
	 */
	public abstract Object getVariable(String key) throws ExecutionException;
}
