package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2String extends AV2Value {
	public V2String(String value, AVarTextCompiler compiler) {this.value=value;this.compiler=compiler;}
	
	@Override public String get() throws CompileException {return value;}
	
	@Override public boolean asBoolean() throws CompileException {
		throw new CompileException("Can not convert String to Boolean");
	}
	@Override public double asDouble() throws CompileException {
		throw new CompileException("Can not convert String to Double");
	}
	@Override public int asInt() throws CompileException {
		throw new CompileException("Can not convert String to Integer");
	}
	@Override public String asString() throws CompileException {return get();}
}
