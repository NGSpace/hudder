package dev.ngspace.hudder.api.compilers.utils;

import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;

public record CompilerEntry(String registry_name, String displayname, boolean unstable, boolean deprecated,
		AHudCompiler<?> compiler) {
	
}
