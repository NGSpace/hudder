package dev.ngspace.hudder.api.variableregistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the registration and retrieval of Hudder system variables.
 * <p>
 * Variables are registered under one or more names and categorized by their
 * associated {@link VariableTypes.Type}. Registered names are converted to
 * lowercase before being stored.
 * </p>
 *
 * @see #registerVariable(DataVariable, VariableTypes.Type, String...)
 */
public class DataVariableRegistry {
	private DataVariableRegistry() {}

	private static Map<String, DataVariable<?>> BooleanVariables = new HashMap<String, DataVariable<?>>();
	private static Map<String, DataVariable<?>> StringVariables = new HashMap<String, DataVariable<?>>();
	private static Map<String, DataVariable<?>> NumberVariables = new HashMap<String, DataVariable<?>>();
	private static Map<String, DataVariable<?>> ObjectVariables = new HashMap<String, DataVariable<?>>();
	private static Map<String, DataVariable<?>> AllVariables = new HashMap<String, DataVariable<?>>();
	
	/**
	 * Registers an object variable under one or more names.
	 * <p>
	 * This is equivalent to registering the variable with
	 * {@link VariableTypes#OBJECT}.
	 * </p>
	 *
	 * @param variable the variable to register
	 * @param names the names under which the variable will be registered
	 * @see #registerVariable(DataVariable, VariableTypes.Type, String...)
	 * 
	 * @deprecated use {@link #registerObjectVariable(DataVariable, String...)}
	 */
	@Deprecated(since = "10.3.0", forRemoval = false)
	public static void registerVariable(DataVariable<Object> variable, String... names) {
		registerVariable(variable, VariableTypes.OBJECT, names);
	}
	
	/**
	 * Registers a variable under one or more names using the specified variable
	 * type.
	 * <p>
	 * Each supplied name is converted to lowercase and added to both the map for
	 * the specified type and the map containing all variables. Registering another
	 * variable with an existing name replaces the previous mapping for that name.
	 * </p>
	 *
	 * @param <T> the value type represented by the specified variable type
	 * @param variable the variable to register
	 * @param type the category under which the variable will be registered
	 * @param names the names under which the variable will be registered
	 * 
	 * @deprecated Use the appropriate register method for the given type. `VariableTypes.Type` is also deprecated.
	 */
	@Deprecated(since = "10.3.0", forRemoval = false)
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
	
	/**
	 * Registers a String variable under one or more names.
	 * <p>
	 * Each supplied name is converted to lowercase and added to both the String
	 * variable registry and the registry containing all variables. Registering
	 * another variable with an existing name replaces the previous mapping for
	 * that name.
	 * </p>
	 *
	 * @param variable the String variable to register
	 * @param names the names under which the variable will be registered
	 */
	public static void registerStringVariable(DataVariable<String> variable, String... names) {
		for (String name : names) {
			StringVariables.put(name.toLowerCase(), variable);
			AllVariables.put(name.toLowerCase(), variable);
		}
	}

	/**
	 * Registers a numeric variable under one or more names.
	 * <p>
	 * Each supplied name is converted to lowercase and added to both the numeric
	 * variable registry and the registry containing all variables. Registering
	 * another variable with an existing name replaces the previous mapping for
	 * that name.
	 * </p>
	 *
	 * @param variable the numeric variable to register
	 * @param names the names under which the variable will be registered
	 */
	public static void registerNumberVariable(DataVariable<Number> variable, String... names) {
		for (String name : names) {
			NumberVariables.put(name.toLowerCase(), variable);
			AllVariables.put(name.toLowerCase(), variable);
		}
	}

	/**
	 * Registers a Boolean variable under one or more names.
	 * <p>
	 * Each supplied name is converted to lowercase and added to both the Boolean
	 * variable registry and the registry containing all variables. Registering
	 * another variable with an existing name replaces the previous mapping for
	 * that name.
	 * </p>
	 *
	 * @param variable the Boolean variable to register
	 * @param names the names under which the variable will be registered
	 */
	public static void registerBooleanVariable(DataVariable<Boolean> variable, String... names) {
		for (String name : names) {
			BooleanVariables.put(name.toLowerCase(), variable);
			AllVariables.put(name.toLowerCase(), variable);
		}
	}

	/**
	 * Registers an object variable under one or more names.
	 * <p>
	 * Each supplied name is converted to lowercase and added to both the object
	 * variable registry and the registry containing all variables. Registering
	 * another variable with an existing name replaces the previous mapping for
	 * that name.
	 * </p>
	 *
	 * @param variable the object variable to register
	 * @param names the names under which the variable will be registered
	 */
	public static void registerObjectVariable(DataVariable<Object> variable, String... names) {
		for (String name : names) {
			ObjectVariables.put(name.toLowerCase(), variable);
			AllVariables.put(name.toLowerCase(), variable);
		}
	}
	
	/**
	 * Retrieves the value of a registered String variable.
	 *
	 * @param key the registered name of the variable
	 * @return the variable's String value, or {@code null} if no String variable
	 *         is registered under the given key
	 * @throws ClassCastException if the variable returns a value that is not a
	 *         String
	 */
	public static String getString(String key) {
		var v = StringVariables.get(key.toLowerCase());
		return v==null ? null : (String) v.getValue(key);
	}
	/**
	 * Retrieves the value of a registered numeric variable as a {@link Double}.
	 *
	 * @param key the registered name of the variable
	 * @return the variable's value converted to a Double, or {@code null} if no
	 *         numeric variable is registered under the given key
	 * @throws ClassCastException if the variable returns a value that is not a
	 *         {@link Number}
	 */
	public static Double getNumber(String key) {
		var v = NumberVariables.get(key.toLowerCase());
		return v==null ? null : ((Number) v.getValue(key)).doubleValue();
	}
	/**
	 * Retrieves the value of a registered Boolean variable.
	 *
	 * @param key the registered name of the variable
	 * @return the variable's Boolean value, or {@code null} if no Boolean variable
	 *         is registered under the given key
	 * @throws ClassCastException if the variable returns a value that is not a
	 *         Boolean
	 */
	public static Boolean getBoolean(String key) {
		var v = BooleanVariables.get(key.toLowerCase());
		return v==null ? null : (Boolean) v.getValue(key);
	}
	/**
	 * Retrieves the value of a registered object variable.
	 *
	 * @param key the registered name of the variable
	 * @return the variable's value, or {@code null} if no object variable is
	 *         registered under the given key
	 */
	public static Object getObject(String key) {
		var v = ObjectVariables.get(key.toLowerCase());
		return v==null ? null : v.getValue(key);
	}
	/**
	 * Retrieves the value of a registered variable regardless of its variable
	 * type.
	 *
	 * @param key the registered name of the variable
	 * @return the variable's value, or {@code null} if no variable is registered
	 *         under the given key
	 */
	public static Object getAny(String key) {
		var v = AllVariables.get(key.toLowerCase());
		return v==null ? null : v.getValue(key);
	}
	
	/**
	 * Checks whether a variable is registered under the given key.
	 *
	 * @param key the registered name to check
	 * @return {@code true} if a variable is registered under the key;
	 *         {@code false} otherwise
	 */
	public static boolean hasVariable(String key) {
		return AllVariables.containsKey(key.toLowerCase());
	}
	
	/**
	 * Returns the total number of registered variable names.
	 * <p>
	 * A variable registered under multiple names contributes one entry for each
	 * distinct name.
	 * </p>
	 *
	 * @return the number of entries in the variable registry
	 */
	public static int getTotalEntriesCount() {
		return AllVariables.size();
	}
}
