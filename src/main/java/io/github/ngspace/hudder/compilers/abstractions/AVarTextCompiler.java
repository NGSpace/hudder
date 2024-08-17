package io.github.ngspace.hudder.compilers.abstractions;

import static io.github.ngspace.hudder.data_management.BooleanData.getBoolean;
import static io.github.ngspace.hudder.data_management.NumberData.getNumber;
import static io.github.ngspace.hudder.data_management.StringData.getString;
import static io.github.ngspace.hudder.util.MathUtils.eval;

import io.github.ngspace.hudder.compilers.utils.CompileException;
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
		Object obj = getStaticVariable(key);
		if (obj==null&&(obj=getDynamicVariable(key))!=null) return obj;
		if (obj!=null) return obj;
		return key;
	}
	
	/**
	 * Processes a string (ex. "1+2+fps") and returns a double if it was able to process the equation, or returns the
	 * variable with that name.
	 * 
	 * @deprecated Please use V2Runtime and V2Value instead of manually processing Math variables.<br>
	 * Will be removed by version 4.0.0
	 * @param variable
	 * @return the result
	 * @throws CompileException 
	 */
	@Deprecated(since = "3.5.0", forRemoval = true)
	public Object getMathVariable(String variable) throws CompileException {
		String advkey = variable.trim().replace(" ", "");
		String[] keys = advkey.split("[^\\d\\.A-z ]");
		if (keys.length==1) return getVariable(variable);
		for (String key : keys) {
			if ("".equals(key)) continue;
			advkey = advkey.replace(key, getVariable(key).toString());
		}
		try {
			return eval(advkey); //Very Expensive operation so try to avoid it as much as possible.
		} catch (Exception e) {
			return getVariable(variable);
		}
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
		Object obj = get(key);
		if (obj!=null) return obj;
		return key;
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