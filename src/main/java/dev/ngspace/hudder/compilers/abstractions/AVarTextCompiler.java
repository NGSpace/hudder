package dev.ngspace.hudder.compilers.abstractions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.main.HudCompilationManager;

public abstract class AVarTextCompiler extends ATextCompiler {
	
	public static Map<String, Object> tempVariables = new HashMap<String, Object>();
	protected Consumer<AHudCompiler<?>> listener = this::preCompileListener;
	
	protected AVarTextCompiler() {
		HudCompilationManager.addPreCompilerListener(listener);
	}
	
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
	
	
	/**
	 * Returns the temporary variables.
	 * 
	 * <br><br>
	 * 
	 * Temporary variables get deleted every hud compiliation.
	 * @param key - the name of the variable
	 * @return the value of the variable or null if it is not set
	 */
	public Object getTempVariable(String key) {return tempVariables.get(key);}
	/**
	 * Sets the value of a temporary variable.
	 * 
	 * <br><br>
	 * 
	 * Temporary variables get deleted every hud compiliation.
	 * @param key - the name of the variable
	 * @param value - the new value of the variable
	 */
	public void putTemp(String key, Object value) {tempVariables.put(key, value);}
	
	@Override
	public void resetState() throws IOException {
		tempVariables.clear();
		super.resetState();
	}
	
	@Override
	public void shutdown() {
		HudCompilationManager.precomplistners.remove(listener);
		super.shutdown();
	}
	
	public void preCompileListener(AHudCompiler<?> comp) {
		tempVariables.clear();
	}
	
}