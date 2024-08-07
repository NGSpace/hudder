package io.github.ngspace.hudder.compilers.hudderv2;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.meta.MethodValue;

public class V2Value extends MethodValue {
	boolean isStatic = false;
	public V2Value(String value, AVarTextCompiler compiler) {
		super(value, compiler);
		isStatic = compiler.isStaticVariable(value);
	}
	public Object toObject() throws CompileException {
		if (isStatic) return compiler.getStaticVariable(value);
		return compiler.getVariable(value);
	}
}
