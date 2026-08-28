package dev.ngspace.hudder.api.compilers;

public record CompilerEntry(String registry_name, String displayname, boolean unstable, boolean deprecated,
		AHudCompiler<?> compiler) {
	
}
