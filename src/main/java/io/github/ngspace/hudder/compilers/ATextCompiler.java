package io.github.ngspace.hudder.compilers;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.config.ConfigInfo;

public abstract class ATextCompiler {

	protected static Map<String, Object> variables = new HashMap<String, Object>();
	
	public abstract CompileResult compile(ConfigInfo info, String text) throws CompileException;
	public abstract Object getVariable(String key) throws CompileException;
	
	public void put(String key, Object value) {variables.put(key, value);}
	public Object get(String key) {return variables.get(key);}
}
