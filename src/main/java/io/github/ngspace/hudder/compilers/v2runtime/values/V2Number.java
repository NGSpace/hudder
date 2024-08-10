package io.github.ngspace.hudder.compilers.v2runtime.values;

import io.github.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2Number extends V2Value {
	double doubleVal;
	public V2Number(double value, AVarTextCompiler compiler) {
		this.value=Double.toString(value);
		this.doubleVal = value;
		this.compiler=compiler;
	}
	@Override public Object get() throws CompileException {return doubleVal;}
}
