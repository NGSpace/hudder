package io.github.ngspace.hudder.compilers.v2runtime.values;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2DynamicVar extends V2Value {
	public V2DynamicVar(String value, AVarTextCompiler compiler) {this.value=value;this.compiler=compiler;}
	
	@Override public Object get() throws CompileException {
		return compiler.getDynamicVariable(value);
	}
	
	@Override public boolean hasValue() {return compiler.get(value)!=null;}
}
