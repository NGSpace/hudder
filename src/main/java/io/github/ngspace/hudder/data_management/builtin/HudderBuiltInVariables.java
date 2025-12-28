package io.github.ngspace.hudder.data_management.builtin;

import io.github.ngspace.hudder.data_management.api.DataVariable;
import io.github.ngspace.hudder.data_management.api.DataVariableRegistry;
import io.github.ngspace.hudder.data_management.api.VariableTypes;

public class HudderBuiltInVariables {
	protected HudderBuiltInVariables() {}
	
	public static void registerVariables() {
		ComputerData.registerVariables();
	}
	public static void register(DataVariable<?> variable, VariableTypes.Type<?> type, String... names) {
		DataVariableRegistry.registerVariable(variable, type, names);
	}
}
