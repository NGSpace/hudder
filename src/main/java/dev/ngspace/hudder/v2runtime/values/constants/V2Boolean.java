package dev.ngspace.hudder.v2runtime.values.constants;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class V2Boolean extends AV2Value {
	
	boolean bvalue;
	
	public V2Boolean(boolean value, AV2Compiler compiler, int line, int charpos, String debugvalue) {
		super(line, charpos, debugvalue, compiler);
		this.bvalue = value;
	}

	@Override public Boolean get() throws ExecutionException {return bvalue;}
	
	@Override public boolean asBoolean() throws ExecutionException {return get();}
	@Override public double asDouble() throws ExecutionException {
		throw new ExecutionException(invalidTypeMessage("Double", value, false),line,charpos);
	}
	@Override public int asInt() throws ExecutionException {
		throw new ExecutionException(invalidTypeMessage("Int", value, false),line,charpos);
	}
	@Override public String asString() throws ExecutionException {
		throw new ExecutionException(invalidTypeMessage("String", value, false),line,charpos);
	}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws ExecutionException {
		throw new ExecutionException("Can't change the value of a boolean constant", line, charpos);
	}
	@Override public boolean isConstant() throws ExecutionException {return true;}
	
}
