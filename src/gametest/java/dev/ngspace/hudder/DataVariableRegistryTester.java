package dev.ngspace.hudder;

import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;

public class DataVariableRegistryTester {
	
	public static void testBoolean(String variable_name, boolean expected_value) {
		var val = DataVariableRegistry.getBoolean(variable_name);
		if (val != expected_value) {
			throw new AssertionError(variable_name + " expected \"" + expected_value + "\" got \"" + val + "\"");
		}
	}
	
	public static void testString(String variable_name, String expected_value) {
		var val = DataVariableRegistry.getString(variable_name);
		if (!expected_value.equals(val)) {
			throw new AssertionError(variable_name + " expected \"" + expected_value + "\" got \"" + val + "\"");
		}
	}
	
	public static void testNumber(String variable_name, double expected_value) {
		var val = DataVariableRegistry.getNumber(variable_name);
		if (val != expected_value) {
			throw new AssertionError(variable_name + " expected \"" + expected_value + "\" got \"" + val + "\"");
		}
	}
	
	public static void testObject(String variable_name, Object expected_value) {
		var val = DataVariableRegistry.getObject(variable_name);
		if (!expected_value.equals(val)) {
			throw new AssertionError(variable_name + " expected \"" + expected_value + "\" got \"" + val + "\"");
		}
	}
}
