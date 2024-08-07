package io.github.ngspace.hudder.compilers;

import static io.github.ngspace.hudder.data_management.BooleanData.getBoolean;
import static io.github.ngspace.hudder.data_management.NumberData.getNumber;
import static io.github.ngspace.hudder.data_management.StringData.getString;
import static io.github.ngspace.hudder.util.MathUtils.eval;

import io.github.ngspace.hudder.config.ConfigManager;

public abstract class AVarTextCompiler extends ATextCompiler {
	
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
		Object obj = getStaticVariable(key);
		if (obj==null) return key;
		return obj;
	}
	public boolean isStaticVariable(String key) {
		return getOperator(key)==null && !key.contains("=") && !key.contains("+") && !key.contains("-")
				&& !key.contains("*") && !key.contains("/") && !key.contains("%");
	}
	public Object getStaticVariable(String key) {
		Object obj = getNumber(key);
		if (obj!=null) return obj;
		if ((obj=getBoolean(key))!=null) return obj;
		if ((obj=getString(key))!=null) return obj;
		if ((obj=ConfigManager.getConfig().globalVariables.get(key))!=null) return obj;
		if ((obj=get(key))!=null) return obj;
		if ("true".equals(key)) return true;
		if ("false".equals(key)) return false;
		return null;
	}
	/**
	 * Dumbest thing I ever wrote... but it works.
	 */
	public String getOperator(String condString) {
		if (condString.contains("==")) return "==";
		if (condString.contains(">=")) return ">=";
		if (condString.contains("<=")) return "<=";
		if (condString.contains(">" )) return ">" ;
		if (condString.contains("<" )) return "<" ;
		if (condString.contains("!=")) return "!=";
		return null;
	}
}