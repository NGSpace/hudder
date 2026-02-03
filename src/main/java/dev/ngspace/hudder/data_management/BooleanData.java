package dev.ngspace.hudder.data_management;

import dev.ngspace.hudder.data_management.api.DataVariableRegistry;

/**
 * @deprecated use DataVariableRegistry.
 */
@Deprecated(since = "9.2.0", forRemoval = true)
public class BooleanData {private BooleanData(){}
	@Deprecated
	public static Boolean getBoolean(String key) {
		return DataVariableRegistry.getBoolean(key);
	}
}