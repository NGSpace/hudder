package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.CompileState;
import io.github.ngspace.hudder.meta.MethodValue;

@FunctionalInterface
public interface IMethod {
	/**
	 * Called when the first parameter passed to MetaCompiler has been a command registered with this object.
	 * @param config - The config used
	 * @param meta - The current meta
	 * @param compiler - The compiler invoking this method
	 * @param args - the parameters supplied to this method
	 * @throws CompileException - if the method is not called properly or is unable to execute.
	 */
	public void invoke(ConfigInfo config, CompileState meta, ATextCompiler compiler, String method, MethodValue... args)
			throws CompileException;
}
