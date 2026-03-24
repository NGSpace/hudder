package dev.ngspace.hudder.v2runtime.values.constants;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class V2String extends AV2Value {
	public V2String(String value, AV2Compiler compiler, int line, int charpos) {super(line, charpos, value,compiler);}
	
	@Override public String get() throws ExecutionException {return value;}
	
	@Override public boolean asBoolean() throws ExecutionException {
		throw new ExecutionException(invalidTypeMessage("Boolean", value, ""),line,charpos);
	}
	@Override public double asDouble() throws ExecutionException {
		throw new ExecutionException(invalidTypeMessage("Double", value, ""),line,charpos);
	}
	@Override public int asInt() throws ExecutionException {
		throw new ExecutionException(invalidTypeMessage("Int", value, ""),line,charpos);
	}
	@Override public String asString() throws ExecutionException {return get();}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws ExecutionException {
		throw new ExecutionException("Can't change the value of a string constant", line, charpos);
	}
	
	@Override public boolean isConstant() throws ExecutionException {return true;}
}
