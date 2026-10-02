package dev.ngspace.hudder.v2runtime.functions;

import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

/**
 * @deprecated Use V3
 */
@SuppressWarnings("removal")
@Deprecated(since = "11.1.0", forRemoval = true)
public interface IV2Function {
	@Deprecated(since = "11.1.0", forRemoval = true)
	public Object execute(V2Runtime runtime, String functionName, AV2Value[] args, int line, int charpos)
			throws ExecutionException;

	@Deprecated(since = "11.1.0", forRemoval = true)
	public default boolean isDeprecated(String funcname) {return false;}
	@Deprecated(since = "11.1.0", forRemoval = true)
	public default String getDeprecationWarning(String funcname) {return "";}
}
