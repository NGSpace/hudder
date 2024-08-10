package io.github.ngspace.hudder.compilers.abstractions;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.config.ConfigInfo;

public abstract class ATextCompiler {

	protected static Map<String, Object> variables = new HashMap<String, Object>();
	
	public abstract CompileResult compile(ConfigInfo info, String text) throws CompileException;
	public abstract Object getVariable(String key) throws CompileException;
	
	public void put(String key, Object value) {variables.put(key, value);}
	public Object get(String key) {return variables.get(key);}
	public abstract boolean conditionCheck(String condition) throws CompileException;
}
