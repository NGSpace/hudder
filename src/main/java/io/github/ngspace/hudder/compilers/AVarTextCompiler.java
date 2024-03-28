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
		// Imma keep this here just in case, this is obsulete now but may be useful later.
//		if ("".equals(key)) return getPureVariable(variable);
//		Object valuetoparse = getPureVariable(key).toString()	;
//		Object obj = null;
//		if (!(valuetoparse instanceof Double))
//			try {
//				obj = Double.parseDouble(String.valueOf(valuetoparse));
//			} catch (NumberFormatException e) {
//				return variable;
//			}
//		if (!(obj instanceof Number))
//			return String.format("§4{Can not complete math operation: '%s' is not a number!}§r", key);
	}
	protected Number getCleanValue(Number val) {
		if (val instanceof Double && (((double)val)%1==0) || val instanceof Float && (((float)val)%1==0))
			return val.longValue();
		return val;
	}
	public Object getPureVariable(String key) throws CompileException {
		return getNonNull(getNumber(key), getString(key), getBoolean(key), getPrimitive(key));
	}
	public Object getPrimitive(String key) {
		return switch (key) {
			case "true": yield true;
			case "false": yield false;
			case "null": yield "null"; //LOL
			
			/* key wasn't found, look at variables map, if non is found there; return the name of the key. */
			default:
				String t = key.toLowerCase().trim();
				Object res = ConfigManager.getConfig().globalVariables.get(t);
				if (res==null) res = get(key);
				yield res != null ? res : key;
		};
	}
	public Object getNonNull(Object... obj) {
		for (Object o : obj) if (o!=null) return o;
		return null;
	}
}
