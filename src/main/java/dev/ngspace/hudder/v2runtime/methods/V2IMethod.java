package dev.ngspace.hudder.v2runtime.methods;

import dev.ngspace.hudder.api.compilers.compilers.AV2Compiler;
import dev.ngspace.hudder.api.compilers.utils.CompileState;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ObjectWrapper;
import dev.ngspace.hudder.v2runtime.V2Runtime;

/**
 * @deprecated Use V3
 */
@Deprecated(since = "11.1.0", forRemoval = true)
@SuppressWarnings("removal")
@FunctionalInterface
public interface V2IMethod {

	@Deprecated(since = "11.1.0", forRemoval = true)
	public default boolean isDeprecated(String name) {
		return false;
	}
	@Deprecated(since = "11.1.0", forRemoval = true)
	public default String getDeprecationWarning(String name) {
		return name + " is Deprecated";
	}
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public void invoke(HudderConfig ci, CompileState meta, AV2Compiler comp, V2Runtime runtime, String type,
			TextPos pos, ObjectWrapper... args) throws ExecutionException;
}
