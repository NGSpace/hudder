package dev.ngspace.hudder.data_management;

import dev.ngspace.hudder.data_management.api.DataVariableRegistry;

/**
 * @deprecated use DataVariableRegistry.
 */
@Deprecated(since = "9.2.0", forRemoval = true)
public class StringData {private StringData() {}
	@Deprecated
	public static Object getString(String key) {
		return DataVariableRegistry.getString(key);
	}
}
