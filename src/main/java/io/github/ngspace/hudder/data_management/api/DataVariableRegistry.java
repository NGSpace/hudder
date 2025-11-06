package io.github.ngspace.hudder.data_management.api;

import java.util.HashMap;

import io.github.ngspace.hudder.data_management.ObjectDataAPI;

public class DataVariableRegistry {
	
	static HashMap<String, DataVariable<?>> registeredVariables = new HashMap<String, DataVariable<?>>();
	
	private DataVariableRegistry() {}
	
	static {
		registerLegacyVariableSystem();
	}
	
	@SuppressWarnings("removal")
	static void registerLegacyVariableSystem() {
		ObjectDataAPI.addObjectGetter(key-> {
			var variable = registeredVariables.get(key);
			if (key!=null)
				return variable.getValue(key);
			return null;
		});
	}
	
	public static <T> void registerVariable(DataVariable<T> variable, VariableType type) {
		
	}
	
	public static enum VariableType {
		OBJECT;
	}
}
