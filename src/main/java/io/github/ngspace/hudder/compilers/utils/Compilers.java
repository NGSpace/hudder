package io.github.ngspace.hudder.compilers.utils;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.config.ConfigManager;

public class Compilers {private Compilers() {}
	
	private static Map<String, String> Comps = new HashMap<String, String>();
	private static final Map<String, ATextCompiler> loadedcomps = new HashMap<String,ATextCompiler>();
	
	private static final String EMPTYCOM = "io.github.ngspace.hudder.compilers.EmptyCompiler";
	private static final String DEFAULT_COMPILER = "io.github.ngspace.hudder.compilers.HudderV2Compiler";
	
	
	static {
		/* Default */
		putCompiler("defaultcompiler", DEFAULT_COMPILER);
		putCompiler("default compiler", DEFAULT_COMPILER);
		putCompiler("default", DEFAULT_COMPILER);
		putCompiler("hudder", DEFAULT_COMPILER);
		putCompiler("hudderv2", DEFAULT_COMPILER);
		
		
		/* JavaScript */
		putCompiler("js", "io.github.ngspace.hudder.compilers.JavaScriptCompiler");
		putCompiler("javascript", "io.github.ngspace.hudder.compilers.JavaScriptCompiler");
		putCompiler("javascriptcompiler", "io.github.ngspace.hudder.compilers.JavaScriptCompiler");
		
		
		putCompiler("empty",EMPTYCOM);
		putCompiler("none", EMPTYCOM);
		putCompiler("null", EMPTYCOM);
		putCompiler( null , EMPTYCOM);
	}
	
	public static ATextCompiler getCompilerFromName(String name) throws ReflectiveOperationException,
		IllegalArgumentException, SecurityException {
		
		String comp = name.toLowerCase();
		if (Comps.get(comp)==null) return getCompilerFromName("default");
		if (!loadedcomps.containsKey(comp)) 
			loadedcomps.put(comp,(ATextCompiler) Class.forName(Comps.get(comp)).getConstructor().newInstance());
		return loadedcomps.get(comp);
	}
	
	/**
	 * Function made for source only, for external compilers use registerCompiler(String, String);
	 * @param name - The name of the compiler, used to save which compiler is selected.
	 * @param classname - The class leading to the compiler.
	 */
	private static void putCompiler(String name, String classname) {Comps.put(name, classname);}
	
	/**
	 * Incase someone wants to add their own compiler without editing source.
	 * @param name - The name of the compiler, used to save which compiler is selected.
	 * @param classname - The class leading to the compiler.
	 */
	public static void registerCompiler(String name, String classname) {
		Comps.put(name.toLowerCase(), classname);
		ConfigManager.getConfig().readConfig();
	}

	public static String get(String str) {
		return Comps.get(str);
	}
	
	
}
