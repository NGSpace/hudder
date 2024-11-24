package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;

public class V2LogicalAND extends AV2Value {
	
	private AV2Value[] values;
	private Boolean constant;

	protected V2LogicalAND(AV2Value[] values, int line, int charpos, String debugvalue, AV2Compiler compiler) throws CompileException {
		super(line, charpos, debugvalue, compiler);
		this.values = values;
		if (isConstant()) constant = get();
	}
	
	@Override public Boolean get() throws CompileException {
		if (constant!=null) return constant;
		for (AV2Value value : values) if (!value.asBoolean()) return false;
		return true;
	}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of a logical or operation", line, charpos);
	}
	@Override public boolean isConstant() throws CompileException {
		for (AV2Value value : values) if (!value.isConstant()) return false;
		return true;
	}
	
}
