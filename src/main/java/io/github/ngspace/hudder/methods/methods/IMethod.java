package io.github.ngspace.hudder.methods.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.utils.ObjectWrapper;

@FunctionalInterface
public interface IMethod {

	public default boolean isDeprecated(String name) {
		return false;
	}
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
	public void invoke(ConfigInfo ci, CompileState meta, ATextCompiler comp, String type, int line, int charpos,
			ObjectWrapper... args) throws CompileException;
}
