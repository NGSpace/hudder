package dev.ngspace.hudder.data_management;

import dev.ngspace.hudder.data_management.api.DataVariableRegistry;

/**
 * @deprecated use DataVariableRegistry.
 */
@Deprecated(since = "9.2.0", forRemoval = true)
public class NumberData {private NumberData() {}
	@Deprecated
	public static Object getNumber(String key) {
		return DataVariableRegistry.getNumber(key);
	}
}
