package dev.ngspace.hudder.api;

import dev.ngspace.hudder.api.compilers.CompilerRegistry;
import dev.ngspace.hudder.config.ImplCompilerRegistry;

public class HudderApi {
	
	private HudderApi() {}
	
	public static CompilerRegistry COMPILER_REGISTRY = new ImplCompilerRegistry();
	
}
