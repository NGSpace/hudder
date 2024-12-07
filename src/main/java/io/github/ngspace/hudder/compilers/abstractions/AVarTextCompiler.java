package io.github.ngspace.hudder.compilers.abstractions;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.data_management.BooleanData;
import io.github.ngspace.hudder.data_management.NumberData;
import io.github.ngspace.hudder.data_management.ObjectData;
import io.github.ngspace.hudder.data_management.StringData;

public abstract class AVarTextCompiler extends ATextCompiler {
	
	
	@Override public Object getVariable(String key) throws CompileException {
		Object obj = getSystemVariable(key);
		if (obj==null&&(obj=getDynamicVariable(key))!=null) return obj;
		if (obj!=null) return obj;
		return key;
	}
	
	/**
	 * If the variable exists within Hudder's predefined variables (ex. fps, x, y, z)
	 * @param key - the name of the variable
	 * @return true or false
	 */
	public boolean isSystemVariable(String key) {return getSystemVariable(key)!=null||"null".equals(key);}
	/**
	 * Returns the value of the variable
	 * @param key - the name of the variable
	 * @return The value of the variable or null if it doesn't exist.
	 */
	public Object getSystemVariable(String key) {
		Object obj = NumberData.getNumber(key);
		if (obj!=null) return obj;
		if ((obj=BooleanData.getBoolean(key))!=null) return obj;
		if ((obj=StringData.getString(key))!=null) return obj;
		if ((obj=ObjectData.getObject(key))!=null) return obj;
		if ((obj=Hudder.config.globalVariables.get(key))!=null) return obj;
		return null;
	}

	public Object getDynamicVariable(String key) {
		Object obj = get(key);
		if (obj!=null) return obj;
		return key;
	}
}