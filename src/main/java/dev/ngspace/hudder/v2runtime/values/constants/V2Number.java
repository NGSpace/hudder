package dev.ngspace.hudder.v2runtime.values.constants;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class V2Number extends AV2Value {
	double doubleVal;
	public V2Number(String value, int line, int charpos, AV2Compiler compiler) {
		super(line, charpos, value, compiler);
		if (value.startsWith("0x"))
			this.doubleVal = Integer.parseUnsignedInt(value.substring(2), 16);
		else if (value.startsWith("#"))
			this.doubleVal = Integer.parseUnsignedInt(value.substring(1), 16);
		else
			this.doubleVal = Double.parseDouble(value);
	}
	
	@Override public Double get() throws ExecutionException {return doubleVal;}
	
	@Override public boolean asBoolean() throws ExecutionException {
		throw new ExecutionException(invalidTypeMessage("Boolean", value, 0d),line,charpos);
	}
	@Override public double asDouble() throws ExecutionException {return get();}
	@Override public int asInt() throws ExecutionException {return get().intValue();}
	@Override public String asString() throws ExecutionException {
		throw new ExecutionException(invalidTypeMessage("String", value, 0d),line,charpos);
	}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws ExecutionException {
		throw new ExecutionException("Can't change the value of a number constant", line, charpos);
	}
	
	@Override public boolean isConstant() throws ExecutionException {return true;}
}
