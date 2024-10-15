package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2Number extends AV2Value {
	double doubleVal;
	public V2Number(double value) {
		this.value=Double.toString(value);
		this.doubleVal = value;
	}
	
	@Override public Double get() throws CompileException {return doubleVal;}
	
	@Override public boolean asBoolean() throws CompileException {
		throw new CompileException(invalidTypeMessage("Boolean", value, 0d));
	}
	@Override public double asDouble() throws CompileException {return get();}
	@Override public int asInt() throws CompileException {return get().intValue();}
	@Override public String asString() throws CompileException {
		throw new CompileException(invalidTypeMessage("String", value, 0d));
	}
}
