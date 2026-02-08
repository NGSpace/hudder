package dev.ngspace.hudder.v2runtime.values;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.utils.CompileException;

public class V2SystemVar extends AV2Value {
	public V2SystemVar(String value, AV2Compiler compiler, int line, int charpos) {
		super(line, charpos, value.toLowerCase(), compiler);
	}
	
	@Override public Object get() throws CompileException {
		return compiler.getSystemVariable(value);
	}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Variable \""+this.value+"\" Can not be changed as it is set by the system", line, charpos);
	}
	
	@Override public boolean isConstant() throws CompileException {return false;}
	
	@Override public boolean hasValue() {return !"unset".equals(value);}
}
