package dev.ngspace.hudder.api.compilers.abstractions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import dev.ngspace.hudder.api.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.interfaces.PreparedCompiler;
import dev.ngspace.hudder.api.compilers.interfaces.VariablesManager;
import dev.ngspace.hudder.api.compilers.interfaces.VariablesProvider;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.config.HudderConfig;

public abstract class AVarTextCompiler extends AHudCompiler<String> implements VariablesProvider, VariablesManager,
		PreparedCompiler {
	
	protected Map<String, Object> tempVariables = new HashMap<String, Object>();
	protected Map<String, Object> variables = new HashMap<String, Object>();
	
	protected AVarTextCompiler(HudderConfig config) {
		super(config, new AtomicReference<>(), new HashMap<>());
	}
	
	@Override public Object get(String key) {return variables.get(key);}
	
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
	
	@Override public Object getVariable(String key) {
		Object obj = DataVariableRegistry.getAny(key);
		if (obj==null&&(obj=getDynamicVariable(key))!=null) return obj;
		if (obj!=null) return obj;
		return key;
	}
	
	@Override public void put(String key, Object value) {variables.put(key, value);}
	
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
	public void reset() throws IOException {
		tempVariables.clear();
		variables.clear();
		super.reset();
	}
	
	@Override
    public void prepareCompiler() {
		tempVariables.clear();
    }
}