package dev.ngspace.hudder.v2runtime.methods;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.main.config.HudderConfig;
import dev.ngspace.hudder.utils.ObjectWrapper;
import dev.ngspace.hudder.v2runtime.V2Runtime;

@FunctionalInterface
public interface V2IMethod {

	public default boolean isDeprecated(String name) {
		return false;
	}
	public default String getDeprecationWarning(String name) {
		return name + " is Deprecated";
	}
	
	public void invoke(HudderConfig ci, CompileState meta, AV2Compiler comp, V2Runtime runtime, String type,
			TextPos pos, ObjectWrapper... args) throws CompileException;
}
