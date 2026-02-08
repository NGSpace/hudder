package dev.ngspace.hudder.v2runtime.functions;

import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class TestFunction implements IV2Function {

	@Override public Object execute(V2Runtime runtime, String functionName, AV2Value[] args, int line, int charpos)
			throws CompileException {
		return args[0].asString() + args[1].asString();
	}
	
}
