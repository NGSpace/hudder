package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2SystemVar extends V2Value {
	public V2SystemVar(String value, AVarTextCompiler compiler) {this.value=value;this.compiler=compiler;}
	
	@Override public Object get() throws CompileException {
		return compiler.getSystemVariable(value);
	}
}
