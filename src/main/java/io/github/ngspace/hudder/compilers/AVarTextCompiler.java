package io.github.ngspace.hudder.compilers;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.data_management.BooleanData;
import io.github.ngspace.hudder.data_management.NumberData;
import io.github.ngspace.hudder.data_management.StringData;

public abstract class AVarTextCompiler extends ATextCompiler {
	
	public boolean isCondition(String key) {
		int i = key.indexOf('=');
		if (i==-1&&!key.contains(">")&&!key.contains("<")) return false;
		if (i==key.length()) return false;
		if (i==0) return false;
		char pre = key.charAt(i-1);
		return pre=='<'||pre=='>'||pre=='!'||key.charAt(i+1)=='=';
	}
	
	@Override public Object getVariable(String key) throws CompileException {
		Object obj = getSystemVariable(key);
		if (obj==null&&(obj=getDynamicVariable(key))!=null) return obj;
		if (obj!=null) return obj;
		return key;
	}
	
	
	//I now realize the name "static variable" might be a little confusing, it means a variable that can't be modified
	public boolean isSystemVariable(String key) {
		return getSystemVariable(key)!=null||"null".equals(key);
	}
	public Object getSystemVariable(String key) {
		Object obj = NumberData.getNumber(key);
		if (obj!=null) return obj;
		if ((obj=BooleanData.getBoolean(key))!=null) return obj;
		if ((obj=StringData.getString(key))!=null) return obj;
		if ((obj=ConfigManager.getConfig().globalVariables.get(key))!=null) return obj;
		if ("null".equals(key)) return null;
		return null;
	}

	public boolean isDynamicVariable(String key) {
		return getSystemVariable(key)==null&&getOperator(key)==null&&!key.contains("+")&&!key.contains("-")
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
		if (condString.contains("!=")) return "!=";
		if (condString.contains(">=")) return ">=";
		if (condString.contains("<=")) return "<=";
		if (condString.contains(">" )) return ">" ;
		if (condString.contains("<" )) return "<" ;
		return null;
	}
}