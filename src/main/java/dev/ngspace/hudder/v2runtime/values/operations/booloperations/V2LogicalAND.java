package dev.ngspace.hudder.v2runtime.values.operations.booloperations;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class V2LogicalAND extends AV2Value {
	
	private AV2Value[] values;
	private Boolean constant;

	public V2LogicalAND(AV2Value[] values, int line, int charpos, String debugvalue, AV2Compiler compiler)
			throws ExecutionException {
		super(line, charpos, debugvalue, compiler);
		this.values = values;
		if (isConstant()) constant = get();
	}
	
	@Override public Boolean get() throws ExecutionException {
		if (constant!=null) return constant;
		for (AV2Value value : values) if (!value.asBoolean()) return false;
		return true;
	}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws ExecutionException {
		throw new ExecutionException("Can't change the value of a logical or operation", line, charpos);
	}
	@Override public boolean isConstant() throws ExecutionException {
		for (AV2Value value : values) if (!value.isConstant()) return false;
		return true;
	}
	
}
