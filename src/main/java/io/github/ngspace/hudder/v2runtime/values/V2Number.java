package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2Number extends V2Value {
	double doubleVal;
	public V2Number(double value, AVarTextCompiler compiler) {
		this.value=Double.toString(value);
		this.doubleVal = value;
		this.compiler=compiler;
	}
	
	@Override public Double get() throws CompileException {return doubleVal;}
	
	@Override public boolean asBoolean() throws CompileException {
		throw new CompileException("Can not convert Double to Boolean");
	}
	@Override public double asDouble() throws CompileException {return get();}
	@Override public int asInt() throws CompileException {return get().intValue();}
	@Override public String asString() throws CompileException {return "";}
}
