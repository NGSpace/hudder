package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.V2Runtime;

public interface IV2VariableParser {
	public AV2Value parse(V2Runtime runtime, String valuee, AV2Compiler comp, int line, int charpos) throws CompileException;
}
