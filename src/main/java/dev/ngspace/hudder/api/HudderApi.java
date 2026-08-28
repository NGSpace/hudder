package dev.ngspace.hudder.api;

import dev.ngspace.hudder.api.compilers.CompilerRegistry;
import dev.ngspace.hudder.api.compilers.Compilers;

public class HudderApi {
	
	private HudderApi() {}
	
	public static CompilerRegistry COMPILER_REGISTRY = new Compilers();
	
}
