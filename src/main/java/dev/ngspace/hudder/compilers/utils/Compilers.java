package dev.ngspace.hudder.compilers.utils;

import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.HudPackCompiler;
import dev.ngspace.hudder.compilers.HudderV2Compiler;
import dev.ngspace.hudder.compilers.JavaScriptCompiler;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;

public class Compilers {private Compilers() {}
	
	private static Map<String, AHudCompiler<?>> registeredcompilers = new HashMap<String,AHudCompiler<?>>();
	
	
	public static void registerDefaultCompilers() {
		registeredcompilers.put("hudder", new HudderV2Compiler());
		registeredcompilers.put("js", new JavaScriptCompiler());
		registeredcompilers.put("pack", new HudPackCompiler());
	}
	
	public static AHudCompiler<?> getCompilerFromName(String name) throws IllegalArgumentException {
		
		String comp = name.toLowerCase();
		if (registeredcompilers.containsKey(comp)) return registeredcompilers.get(comp);
		
		throw new IllegalArgumentException("Compiler named \"" + name + "\" either does not exist or has not yet been loaded.");
	}
	
	/**
	 * @deprecated use {@code #registerCompiler(String, ATextCompiler)}
	 */
	@Deprecated(since = "9.0.0", forRemoval = true)
	public static void registerCompiler(String name, String classname) {
		try {
			registerCompiler(name,(AHudCompiler<?>) Class.forName(classname).getConstructor().newInstance());
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Failed to load compiler", e);
		}
	}
	
	public static void registerCompiler(String name, AHudCompiler<?> compiler) {
		registeredcompilers.put(name.toLowerCase(), compiler);
		Hudder.config.readAndUpdateConfig();
	}
	
	public static boolean has(String name) {
		return registeredcompilers.get(name.toLowerCase())!=null;
	}
	
	
}
