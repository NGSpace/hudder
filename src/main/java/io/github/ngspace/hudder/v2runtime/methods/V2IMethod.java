package io.github.ngspace.hudder.v2runtime.methods;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler.CharPosition;
import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.main.config.HudderConfig;
import io.github.ngspace.hudder.utils.ObjectWrapper;
import io.github.ngspace.hudder.v2runtime.V2Runtime;

@FunctionalInterface
public interface V2IMethod {

	public default boolean isDeprecated(String name) {
		return false;
	}
	public default String getDeprecationWarning(String name) {
		return name + " is Deprecated";
	}
	
	public void invoke(HudderConfig ci, CompileState meta, AV2Compiler comp, V2Runtime runtime, String type,
			CharPosition pos, ObjectWrapper... args) throws CompileException;
}
