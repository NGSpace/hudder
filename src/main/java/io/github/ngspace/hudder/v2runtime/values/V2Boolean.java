package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2Boolean extends AV2Value {
	
	boolean bvalue;
	
	public V2Boolean(boolean value, AVarTextCompiler compiler) {this.bvalue = value;this.compiler = compiler;}

	@Override public Boolean get() throws CompileException {return bvalue;}
	
}
