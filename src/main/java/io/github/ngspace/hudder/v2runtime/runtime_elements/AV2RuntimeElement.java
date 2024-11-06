package io.github.ngspace.hudder.v2runtime.runtime_elements;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;

//TO DO Find a better name for this class
public abstract class AV2RuntimeElement {
	/**
	 * Execute the runtime element.
	 * @param compileState - The state of execution
	 * @param builder - The text builder, append to it when wanting to add text
	 * @return Whether execution should continue.
	 * @throws CompileException
	 */
	public abstract boolean execute(CompileState compileState, StringBuilder builder) throws CompileException;
}
