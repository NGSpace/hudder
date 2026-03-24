package dev.ngspace.hudder.compilers.abstractions;

import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;

public abstract class AHudCompiler<T> {

	public static Map<String, Object> variables = new HashMap<String, Object>();
	
	public abstract T processFile(String filepath) throws CompileException;
	public abstract HudInformation execute(HudderConfig info, T processedfile, String filename) throws ExecutionException;
	public abstract Object getVariable(String key) throws ExecutionException;
	
	
	public abstract boolean setupHudSettings(NGSMCConfigCategory hudsettings);
	
	public HudInformation processAndExecute(HudderConfig config, String filepath, String filename)
			throws CompileException, ExecutionException {
		return execute(config, processFile(filepath), filename);
	}
}
