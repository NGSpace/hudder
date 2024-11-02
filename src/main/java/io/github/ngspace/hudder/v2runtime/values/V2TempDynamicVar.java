package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;

public class V2TempDynamicVar extends AV2Value {

	public V2TempDynamicVar(String value, AV2Compiler compiler, int line, int charpos) {
		super(line, charpos, value.toLowerCase(), compiler);
	}
	
	@Override public Object get() throws CompileException {
		return compiler.getTempVariable(value);
	}
	
	@Override public boolean hasValue() {return compiler.get(value)!=null;}

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
