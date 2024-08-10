package io.github.ngspace.hudder.compilers.v2runtime.values;

import io.github.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2String extends V2Value {
	public V2String(String value, AVarTextCompiler compiler) {this.value=value;this.compiler=compiler;}
	@Override public Object get() throws CompileException {return value;}
}
