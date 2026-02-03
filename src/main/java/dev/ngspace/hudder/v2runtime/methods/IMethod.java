package dev.ngspace.hudder.v2runtime.methods;

import dev.ngspace.hudder.compilers.abstractions.ATextCompiler;
import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.abstractions.ATextCompiler.CharPosition;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.main.config.HudderConfig;
import dev.ngspace.hudder.utils.ObjectWrapper;
import dev.ngspace.hudder.v2runtime.V2Runtime;

/**
 * @deprecated use {@link V2IMethod}
 */
@FunctionalInterface
@Deprecated(since = "9.2.0", forRemoval = false)
public interface IMethod extends V2IMethod {

	@Deprecated
	@Override
	public default boolean isDeprecated(String name) {
		return false;
	}
	@Deprecated
	@Override
	public default String getDeprecationWarning(String name) {
		return name + " is Deprecated";
	}
	
	/**
	 * Called when the first parameter passed to MetaCompiler has been a command registered with this object.
	 * @param config - The config used
	 * @param meta - The current meta
	 * @param compiler - The compiler invoking this method
	 * @param args - the parameters supplied to this method
	 * @throws CompileException - if the method is not called properly or is unable to execute.
	 */
	@Deprecated
	public void invoke(HudderConfig ci, CompileState meta, ATextCompiler comp, String type, int line, int charpos,
			ObjectWrapper... args) throws CompileException;
	
	@Deprecated
	@Override
	default void invoke(HudderConfig ci, CompileState meta, AV2Compiler comp, V2Runtime runtime, String type,
			CharPosition pos, ObjectWrapper... args) throws CompileException {
		invoke(ci, meta, comp, type, pos.line, pos.charpos, args);
	}
}
