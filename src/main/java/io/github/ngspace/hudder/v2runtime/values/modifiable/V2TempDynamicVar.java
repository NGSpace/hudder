package io.github.ngspace.hudder.v2runtime.values.modifiable;

import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class V2TempDynamicVar extends AV2Value {

	public V2TempDynamicVar(String value, AV2Compiler compiler, int line, int charpos) {
		super(line, charpos, value.toLowerCase(), compiler);
	}
	
	@Override public Object get() throws CompileException {
		Object o = compiler.getTempVariable(value);
		if (o==null) return 0;
		return o;
	}

	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		compiler.putTemp(this.value, value);
	}
	
	@Override public boolean isConstant() throws CompileException {return false;}
	
	/*
	 * This is done to allow {_temp=_temp+1} even when {_temp} is not set
	 */
	@Override public double asDouble() throws CompileException {
		try {
			return super.asDouble();
		} catch (Exception e) {
			return 0;
		}
	}

}
