package dev.ngspace.hudder.v2runtime.values;

import dev.ngspace.hudder.api.compilers.compilers.AV2Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.V2Runtime;

/**
 * @deprecated Use V3
 */
@Deprecated(since = "11.1.0", forRemoval = true)
@SuppressWarnings("removal")
public interface IV2VariableParser {
	@Deprecated(since = "11.1.0", forRemoval = true)
	public AV2Value parse(V2Runtime runtime, String valuee, AV2Compiler comp, int line, int charpos)
			throws ExecutionException;
}
