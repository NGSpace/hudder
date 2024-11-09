package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;

public class V2PostIncDecOperator extends AV2Value {
	private AV2Value key;
	private AV2Compiler compilerv2;
	private boolean add;

	protected V2PostIncDecOperator(AV2Value value, AV2Compiler compilerv2, int line, int charpos, boolean add,
			String debugvalue) {
		super(line, charpos, debugvalue, compilerv2);
		this.key = value;
		this.compilerv2 = compilerv2;
		this.add = add;
	}

	@Override public Object get() throws CompileException {
		double val = key.asDouble();
		key.setValue(compilerv2, add ? key.asDouble() + 1 : key.asDouble() -1);
		return val;
	}
	@Override public boolean isConstant() throws CompileException {return key.isConstant();}
	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of a post-inc-dec operator", line, charpos);	
	}
}
