package io.github.ngspace.hudder.data_management.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Hudder's class for management of system variables
 * <br><br>
 * Use {@link #registerVariable(DataVariable, io.github.ngspace.hudder.data_management.api.VariableTypes.Type, String...)}
 * to register variables
 */
public class DataVariableRegistry {
	private DataVariableRegistry() {}

	private static Map<String, DataVariable<?>> BooleanVariables = new HashMap<String, DataVariable<?>>();
	private static Map<String, DataVariable<?>> StringVariables = new HashMap<String, DataVariable<?>>();
	private static Map<String, DataVariable<?>> NumberVariables = new HashMap<String, DataVariable<?>>();
	private static Map<String, DataVariable<?>> ObjectVariables = new HashMap<String, DataVariable<?>>();
	private static Map<String, DataVariable<?>> AllVariables = new HashMap<String, DataVariable<?>>();
	
	public static void registerVariable(DataVariable<Object> variable, String... names) {
		registerVariable(variable, VariableTypes.OBJECT, names);
	}
	
	/**
	 * Registers a variable by the given names in the given variable type.
	 * 
	 * @param <T> - The type of object the variable returns
	 * @param variable - The variable function
	 * @param type - The type of variable (Make sure this matches with <T> to prevent issues)
	 * @param names - The names of the variable
	 */
	public static <T> void registerVariable(DataVariable<?> variable, VariableTypes.Type<T> type, String... names) {
		Map<String, DataVariable<?>> typemap = ObjectVariables;
		if (type == VariableTypes.NUMBER)
			typemap = NumberVariables;
		if (type == VariableTypes.STRING)
			typemap = StringVariables;
		if (type == VariableTypes.BOOLEAN)
			typemap = BooleanVariables;
		for (String name : names) {
			typemap.put(name.toLowerCase(), variable);
			AllVariables.put(name.toLowerCase(), variable);
		}
	}
	
	public static String getString(String key) {
		var v = StringVariables.get(key);
		return v==null ? null : (String) v.getValue(key);
	}
	public static Double getNumber(String key) {
		var v = NumberVariables.get(key);
		return v==null ? null : ((Number) v.getValue(key)).doubleValue();
	}
	public static Boolean getBoolean(String key) {
		var v = BooleanVariables.get(key);
		return v==null ? null : (Boolean) v.getValue(key);
	}
	public static Object getObject(String key) {
		var v = ObjectVariables.get(key);
		return v==null ? null : v.getValue(key);
	}
	public static Object getAny(String key) {
		var v = AllVariables.get(key);
		return v==null ? null : v.getValue(key);
	}
	
	public static boolean hasVariable(String key) {
		return AllVariables.containsKey(key);
	}

	public static int getTotalEntriesCount() {
		return AllVariables.size();
	}
}
