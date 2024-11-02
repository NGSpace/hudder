package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;

public class V2DynamicVar extends AV2Value {
	public V2DynamicVar(String value, AV2Compiler compiler, int line, int charpos) {
		super(line, charpos, value.toLowerCase(), compiler);
	}
	
	@Override public Object get() throws CompileException {
		return compiler.getDynamicVariable(value);
	}
	
	@Override public boolean hasValue() {return compiler.get(value)!=null;}

	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		compiler.put(this.value, value);
	}
	
	@Override public boolean isConstant() throws CompileException {return false;}
}
