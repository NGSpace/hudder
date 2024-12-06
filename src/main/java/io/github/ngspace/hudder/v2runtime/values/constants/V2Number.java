package io.github.ngspace.hudder.v2runtime.values.constants;

import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class V2Number extends AV2Value {
	double doubleVal;
	public V2Number(String value, int line, int charpos, AV2Compiler compiler) {
		super(line, charpos, value, compiler);
		if (value.startsWith("0x")||value.startsWith("#"))
			this.doubleVal = Long.decode(value);
		else
			this.doubleVal = Double.parseDouble(value);
	}
	
	@Override public Double get() throws CompileException {return doubleVal;}
	
	@Override public boolean asBoolean() throws CompileException {
		throw new CompileException(invalidTypeMessage("Boolean", value, 0d),line,charpos);
	}
	@Override public double asDouble() throws CompileException {return get();}
	@Override public int asInt() throws CompileException {return get().intValue();}
	@Override public String asString() throws CompileException {
		throw new CompileException(invalidTypeMessage("String", value, 0d),line,charpos);
	}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of a number constant", line, charpos);
	}
	
	@Override public boolean isConstant() throws CompileException {return true;}
}
