package dev.ngspace.hudder.v2runtime.runtime_elements;

import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.v2runtime.V2Runtime;

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
	
	public boolean returnsAValue() {return false;}
	protected V2Runtime nestedRuntime;

	public V2Runtime getNestedRuntime() {return nestedRuntime;}
}
