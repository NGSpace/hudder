package io.github.ngspace.hudder.compilers.utils;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.ATextCompiler;

public class Compilers {private Compilers() {}
	
	private static Map<String, String> Comps = new HashMap<String, String>();
	private static final Map<String, ATextCompiler> loadedcomps = new HashMap<String,ATextCompiler>();
	
	private static final String EMPTY_COMPILER = "io.github.ngspace.hudder.compilers.EmptyCompiler";
	private static final String DEFAULT_COMPILER = "io.github.ngspace.hudder.compilers.HudderV2Compiler";
	private static final String JAVASCRIPT_COMPILER = "io.github.ngspace.hudder.compilers.JavaScriptCompiler";
	
	
	static {
		/* Default */
		putCompiler("defaultcompiler", DEFAULT_COMPILER);
		putCompiler("default compiler", DEFAULT_COMPILER);
		putCompiler("default", DEFAULT_COMPILER);
		putCompiler("hudder", DEFAULT_COMPILER);
		
		
		/* JavaScript */
		putCompiler("js", JAVASCRIPT_COMPILER);
		putCompiler("javascript", JAVASCRIPT_COMPILER);
		
		/* Empty */
		putCompiler("empty",EMPTY_COMPILER);
		putCompiler("none", EMPTY_COMPILER);
		putCompiler("null", EMPTY_COMPILER);
		putCompiler( null , EMPTY_COMPILER);
	}
	
	public static ATextCompiler getCompilerFromName(String name) throws ReflectiveOperationException,
		IllegalArgumentException, SecurityException {
		
		String comp = name.toLowerCase();
		if (loadedcomps.containsKey(comp)) return loadedcomps.get(comp);
		if (Comps.containsKey(comp)) {
			loadedcomps.put(comp, (ATextCompiler) Class.forName(Comps.get(comp)).getConstructor().newInstance());
			return loadedcomps.get(comp);
		}
		System.out.println(name);
		
		return getCompilerFromName("default");// Fallback to default compiler
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
		Hudder.config.readConfig();
	}

	
	/**
	 * Incase someone wants to add their own compiler without editing source.
	 * @deprecated use registerCompiler()
	 * @param name - The name of the compiler, used to save which compiler is selected.
	 * @param classname - The class leading to the compiler.
	 */
	@Deprecated(since = "4.0.0", forRemoval = true)
	public static void registerLoadedCompiler(String name, ATextCompiler compiler) {
		loadedcomps.put(name.toLowerCase(), compiler);
		Hudder.config.readConfig();
	}
	
	public static boolean has(String name) {
		return loadedcomps.get(name.toLowerCase())!=null||Comps.get(name.toLowerCase())!=null;
	}
	
	
}
