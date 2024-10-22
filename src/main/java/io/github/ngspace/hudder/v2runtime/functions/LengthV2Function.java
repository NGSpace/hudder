package io.github.ngspace.hudder.v2runtime.functions;

import java.util.Collection;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class LengthV2Function implements IV2Function {

	@Override public Object execute(V2Runtime runtime, String functionName, AV2Value[] args, int line, int charpos)
			throws CompileException {
		Object value = args[0].get();
		if (value instanceof Collection<?> c) return c.size();
		return args[0].asString().length();
	}
	
}
