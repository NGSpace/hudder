package dev.ngspace.hudder.compilers.abstractions;

import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;

public abstract class AHudCompiler<T> {

	public static Map<String, Object> variables = new HashMap<String, Object>();
	
	public abstract T processFile(String filepath);
	public abstract HudInformation compile(HudderConfig info, T processedfile, String filename) throws CompileException;
	public abstract Object getVariable(String key) throws CompileException;
	
	public void put(String key, Object value) {variables.put(key, value);}
	public Object get(String key) {return variables.get(key);}
	
	
	public HudderConfig getConfig() {
		return Hudder.config;
	}
	public abstract boolean setupHudSettings(NGSMCConfigCategory hudsettings);
	
	public HudInformation processAndCompile(HudderConfig config, String filepath, String filename) throws CompileException {
		return compile(config, processFile(filepath), filename);
	}
}
