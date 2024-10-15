package io.github.ngspace.hudder.v2runtime.functions;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class TestFunction implements IV2Function {

	@Override
	public Object execute(V2Runtime runtime, String functionName, AV2Value[] args) throws CompileException {
		return args[0].asString() + args[1].asString();
	}
	
}
