package io.github.ngspace.hudder.hudderv2.hudder_runtime_elements;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;

public class BreakV2RuntimeElement extends AV2RuntimeElement {
	@Override public boolean execute(CompileState compileState, StringBuilder builder) throws CompileException {
		compileState.addString(builder.toString(), false);
		builder.setLength(0);
		return false;
	}
}
