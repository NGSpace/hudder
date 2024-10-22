package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2SystemVar extends AV2Value {
	public V2SystemVar(String value, AVarTextCompiler compiler, int line, int charpos) {
		super(line, charpos);
		this.value=value;
		this.compiler=compiler;
	}
	
	@Override public Object get() throws CompileException {
		return compiler.getSystemVariable(value);
	}
	
	@Override public void setValue(AVarTextCompiler compiler, Object value) throws CompileException {
		throw new CompileException("Variable \""+this.value+"\" Can not be changed as it is set by the system", line, charpos);
	}
}
