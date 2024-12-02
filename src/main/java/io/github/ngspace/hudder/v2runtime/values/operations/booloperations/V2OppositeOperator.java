package io.github.ngspace.hudder.v2runtime.values.operations.booloperations;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class V2OppositeOperator extends AV2Value {
	
	private AV2Value bool;

	public V2OppositeOperator(AV2Value value, int line, int charpos, String debugvalue, AV2Compiler compiler) {
		super(line, charpos, debugvalue, compiler);
		this.bool = value;
	}

	@Override public Boolean get() throws CompileException {return !bool.asBoolean();}
	@Override public boolean isConstant() throws CompileException {return bool.isConstant();}

	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of a not operator", line, charpos);
	}
}
