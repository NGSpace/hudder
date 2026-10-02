package dev.ngspace.hudder.v2runtime.runtime_elements;

import dev.ngspace.hudder.api.compilers.utils.CompileState;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.V2Runtime;

/**
 * @deprecated Use V3
 */
@Deprecated(since = "11.1.0", forRemoval = true)
@SuppressWarnings("removal")
public abstract class AV2RuntimeElement {
	/**
	 * Execute the runtime element.
	 * @param compileState - The state of execution
	 * @param builder - The text builder, append to it when wanting to add text
	 * @return Whether execution should continue.
	 * @throws CompileException
	 */
	@Deprecated(since = "11.1.0", forRemoval = true)
	public abstract boolean execute(CompileState compileState, StringBuilder builder) throws ExecutionException;
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public boolean returnsAValue() {return false;}
	/**
	 * @deprecated use nestedRuntimes
	 */
	@Deprecated(since = "10.1.0", forRemoval = true)
	protected V2Runtime nestedRuntime;
	@Deprecated(since = "11.1.0", forRemoval = true)
	protected V2Runtime[] nestedRuntimes;
	
	/**
	 * @deprecated use getNestedRuntimes()
	 */
	@Deprecated(since = "10.1.0", forRemoval = true)
	public V2Runtime getNestedRuntime() {return nestedRuntime;}
	@Deprecated(since = "11.1.0", forRemoval = true)
	public V2Runtime[] getNestedRuntimes() {return nestedRuntimes;}
}
