package dev.ngspace.hudder.api;

import dev.ngspace.hudder.api.compilers.CompilerRegistry;
import dev.ngspace.hudder.config.ImplCompilerRegistry;

/**
 * Main class for accessing the Hudder API... Well, it's supposed to at least, for now it
 * only contains the CompilerRegistry
 * 
 */
public class HudderApi {
	
	private HudderApi() {}
	
	/**
	 * The main registry for Hudder Compilers
	 */
	public static final CompilerRegistry COMPILER_REGISTRY = new ImplCompilerRegistry();
	
}
