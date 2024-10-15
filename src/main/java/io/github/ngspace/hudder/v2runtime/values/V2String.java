package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2String extends AV2Value {
	public V2String(String value, AVarTextCompiler compiler) {this.value=value;this.compiler=compiler;}
	
	@Override public String get() throws CompileException {return value;}
	
	@Override public boolean asBoolean() throws CompileException {
		throw new CompileException(invalidTypeMessage("Boolean", value, ""));
	}
	@Override public double asDouble() throws CompileException {
		throw new CompileException(invalidTypeMessage("Double", value, ""));
	}
	@Override public int asInt() throws CompileException {
		throw new CompileException(invalidTypeMessage("Int", value, ""));
	}
	@Override public String asString() throws CompileException {return get();}
}
