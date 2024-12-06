package io.github.ngspace.hudder.v2runtime.values.constants;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class V2String extends AV2Value {
	public V2String(String value, AV2Compiler compiler, int line, int charpos) {super(line, charpos, value,compiler);}
	
	@Override public String get() throws CompileException {return value;}
	
	@Override public boolean asBoolean() throws CompileException {
		throw new CompileException(invalidTypeMessage("Boolean", value, ""),line,charpos);
	}
	@Override public double asDouble() throws CompileException {
		throw new CompileException(invalidTypeMessage("Double", value, ""),line,charpos);
	}
	@Override public int asInt() throws CompileException {
		throw new CompileException(invalidTypeMessage("Int", value, ""),line,charpos);
	}
	@Override public String asString() throws CompileException {return get();}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of a string constant", line, charpos);
	}
	
	@Override public boolean isConstant() throws CompileException {return true;}
}
