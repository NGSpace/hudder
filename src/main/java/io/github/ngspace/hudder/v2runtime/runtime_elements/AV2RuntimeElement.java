package io.github.ngspace.hudder.v2runtime.runtime_elements;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;

//TO DO Find a better name for this class
public abstract class AV2RuntimeElement {
	public abstract void execute(CompileState meta, StringBuilder builder) throws CompileException;
}
