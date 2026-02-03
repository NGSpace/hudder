package dev.ngspace.hudder.compilers.abstractions;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.data_management.ObjectDataAPI;
import dev.ngspace.hudder.data_management.api.DataVariableRegistry;

public abstract class AVarTextCompiler extends ATextCompiler {
	
	
	@Override public Object getVariable(String key) throws CompileException {
		Object obj = getSystemVariable(key);
		if (obj==null&&(obj=getDynamicVariable(key))!=null) return obj;
		if (obj!=null) return obj;
		return key;
	}
	
	/**
	 * If the variable exists within Hudder's system variables (ex. fps, x, y, z)
	 * @param key - the name of the variable
	 * @return true or false
	 */
	public boolean isSystemVariable(String key) {
		return getSystemVariable(key)!=null||"null".equals(key)||DataVariableRegistry.hasVariable(key);
	}
	/**
	 * Returns the value of the variable
	 * @param key - the name of the variable
	 * @return The value of the variable or null if it doesn't exist.
	 */
	@SuppressWarnings("removal")
	public Object getSystemVariable(String key) {
		Object obj = DataVariableRegistry.getAny(key);
		if (obj!=null) return obj;
		if ((obj=ObjectDataAPI.getObject(key))!=null) return obj;
		return Hudder.config.globalVariables.get(key);
	}

	public Object getDynamicVariable(String key) {
		Object obj = get(key);
		if (obj!=null) return obj;
		return key;
	}
}