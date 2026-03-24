package dev.ngspace.hudder.compilers.abstractions;

import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;

public abstract class AVarTextCompiler extends ATextCompiler {
	
	
	@Override public Object getVariable(String key) {
		Object obj = DataVariableRegistry.getAny(key);
		if (obj==null&&(obj=getDynamicVariable(key))!=null) return obj;
		if (obj!=null) return obj;
		return key;
	}
	
	public void put(String key, Object value) {variables.put(key, value);}
	public Object get(String key) {return variables.get(key);}
	
	/**
	 * If the variable exists within Hudder's system variables (ex. fps, x, y, z)
	 * @param key - the name of the variable
	 * @return true or false
	 */
	public boolean isSystemVariable(String key) {
		return "null".equals(key)||DataVariableRegistry.hasVariable(key);
	}

	public Object getDynamicVariable(String key) {
		Object obj = get(key);
		if (obj!=null) return obj;
		return key;
	}
}