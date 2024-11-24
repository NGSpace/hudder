package io.github.ngspace.hudder.v2runtime.functions;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public interface IV2Function {
	public Object execute(V2Runtime runtime, String functionName, AV2Value[] args, int line, int charpos) throws CompileException;

	public default boolean isDeprecated(String funcname) {return false;}
	public default String getDeprecationWarning(String funcname) {return "";}
}
