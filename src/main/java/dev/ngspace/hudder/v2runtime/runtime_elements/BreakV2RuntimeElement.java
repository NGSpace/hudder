package dev.ngspace.hudder.v2runtime.runtime_elements;

import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.CompileState;

public class BreakV2RuntimeElement extends AV2RuntimeElement {
	@Override public boolean execute(CompileState compileState, StringBuilder builder) throws CompileException {
		compileState.addString(builder.toString(), false);
		builder.setLength(0);
		return false;
	}
}
