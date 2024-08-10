package io.github.ngspace.hudder.compilers;

import static io.github.ngspace.hudder.data_management.BooleanData.getBoolean;
import static io.github.ngspace.hudder.data_management.NumberData.getNumber;
import static io.github.ngspace.hudder.data_management.StringData.getString;
import static io.github.ngspace.hudder.util.MathUtils.eval;

import io.github.ngspace.hudder.config.ConfigManager;

public abstract class AVarTextCompiler extends ATextCompiler {
	
	public boolean isFirstEqualsCondition(String key) {
		int i = key.indexOf('=');
		if (i==-1&&!key.contains(">")&&!key.contains("<")) return false;
		if (i==key.length()) return false;
		if (i==0) return false;
		char pre = key.charAt(i-1);
		return pre=='<'||pre=='>'||pre=='!'||key.charAt(i+1)=='=';
	}
	
	@Override public Object getVariable(String key) throws CompileException {
		if (isFirstEqualsCondition(key)) return conditionCheck(key);
		String[] values = key.split("=",2);
		if (values.length==1) return getMathVariable(key);
		Object newval = getVariable(values[1]);
		put(values[0].trim(), newval);
		return newval;
	}
	
	public Object getMathVariable(String variable) {
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
	private Object getPureVariable(String key) {
		Object obj = getStaticVariable(key);
		if (obj==null&&(obj=getDynamicVariable(key))!=null) return obj;
		if (obj!=null) return obj;
		return key;
	}
	//I now realize the name "static variable" might be a little confusing, it means a variable that can't be modified
	public boolean isStaticVariable(String key) {
		return getStaticVariable(key)!=null;
	}
	public Object getStaticVariable(String key) {
		Object obj = getNumber(key);
		if (obj!=null) return obj;
		if ((obj=getBoolean(key))!=null) return obj;
		if ((obj=getString(key))!=null) return obj;
		if ((obj=ConfigManager.getConfig().globalVariables.get(key))!=null) return obj;
		if ("true".equals(key)) return true;
		if ("false".equals(key)) return false;
		return null;
	}
	public boolean isDynamicVariable(String key) {
		return getStaticVariable(key)==null&&getOperator(key)==null&&!key.contains("+")&&!key.contains("-")
				&&!key.contains("/")&&!key.contains("*")&&!key.contains("%")&&!key.contains("=");
	}
	public Object getDynamicVariable(String key) {
		return get(key);
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