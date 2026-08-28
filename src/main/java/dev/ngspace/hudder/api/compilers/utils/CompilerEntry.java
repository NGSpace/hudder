package dev.ngspace.hudder.api.compilers.utils;

import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;

public record CompilerEntry(String id, String display_name, boolean unstable, boolean deprecated,
		AHudCompiler<?> compiler) {
	
}
