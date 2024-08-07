package io.github.ngspace.hudder.compilers.hudderv2;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.meta.MethodHandler.Value;

public class V2Value extends Value {
	final V2Runtime runtime;
	final String value;
	public V2Value(String value, V2Runtime runtime) {
		this.runtime = runtime;
		this.value = value;
	}
	public Object toObject() throws CompileException {
		return runtime.compiler.getVariable(value);
	}
}
