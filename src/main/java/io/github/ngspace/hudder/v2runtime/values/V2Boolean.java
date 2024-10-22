package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;

public class V2Boolean extends AV2Value {
	
	boolean bvalue;
	
	public V2Boolean(boolean value, AVarTextCompiler compiler, int line, int charpos) {
		super(line,charpos);
		this.bvalue = value;
		this.compiler = compiler;
	}

	@Override public Boolean get() throws CompileException {return bvalue;}
	
	@Override public boolean asBoolean() throws CompileException {return get();}
	@Override public double asDouble() throws CompileException {
		throw new CompileException(invalidTypeMessage("Double", value, false),line,charpos);
	}
	@Override public int asInt() throws CompileException {
		throw new CompileException(invalidTypeMessage("Int", value, false),line,charpos);
	}
	@Override public String asString() throws CompileException {
		throw new CompileException(invalidTypeMessage("String", value, false),line,charpos);
	}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of a boolean constant", line, charpos);
	}
	@Override public boolean isConstant() throws CompileException {return true;}
	
}
