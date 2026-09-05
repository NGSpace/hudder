package dev.ngspace.hudder.api.compilers.interfaces;

public interface PreparedCompiler {
	/**
	 * Called before the Compilation manager starts compiling the main hud.
	 * 
	 * <api-note>This is called before the main hud compiles, not when any hud compiles.
	 * This is preferable to Compilers setting up their own compilation listeners.</api-note>
	 */
	public void prepareCompiler();
}
