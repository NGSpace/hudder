package io.github.ngspace.hudder.compilers.hudderv2.runtime_elements;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.meta.Meta;

//TO DO Find a better name for this class
public abstract class AV2RuntimeElement {
	public abstract void execute(Meta meta, StringBuilder builder) throws CompileException;
}
