package io.github.ngspace.hudder.compilers;

import static io.github.ngspace.hudder.data_management.BooleanData.getBoolean;
import static io.github.ngspace.hudder.data_management.NumberData.getNumber;
import static io.github.ngspace.hudder.data_management.StringData.getString;
import static io.github.ngspace.hudder.util.MathUtils.eval;

import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.config.ConfigManager;

public abstract class AVarTextCompiler extends ATextCompiler {
	public static ConfigInfo info = ConfigManager.getConfig();
	
	@Override public Object getVariable(String variable) throws CompileException {
		String advkey = variable.trim().replace(" ", "");
		String[] keys = advkey.split("[^\\d\\.A-z ]");
		if (keys.length==1) return getPureVariable(variable);
		for (String key : keys) {
			if ("".equals(key)) continue;
			advkey = advkey.replace(key, getPureVariable(key).toString());
		}
		try {
			return eval(advkey); //Very Expensive operation so try to avoid it as much as possible.
		} catch (Exception e) {
			return getPureVariable(variable);
		}
	}
	public Object getPureVariable(String key) {
		Object obj = getNumber(key);
		if (obj!=null) return obj;
		if ((obj=getBoolean(key))!=null) return obj;
		if ((obj=getString(key))!=null) return obj;
		if ((obj=get(key))!=null) return obj;
		if ((obj=ConfigManager.getConfig().globalVariables.get(key))!=null) return obj;
		return getPrimitive(key);
	}
	public Object getPrimitive(String key) {
		/* If key wasn't found, return the name of the key. */
		return switch (key) {case "true" -> true; case "false" -> false; default -> key;};
	}
}