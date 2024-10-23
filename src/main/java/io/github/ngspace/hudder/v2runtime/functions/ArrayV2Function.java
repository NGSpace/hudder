package io.github.ngspace.hudder.v2runtime.functions;

import java.util.ArrayList;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class ArrayV2Function implements IV2Function {
	
	@Override
	public Object execute(V2Runtime runtime, String functionName, AV2Value[] args, int line, int charpos)
			throws CompileException {
		var lst = new ArrayList<Object>();
		for (char c : args[0].asString().toCharArray()) lst.add(c);
		return lst;
	}
	
}
