package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.functions.IV2Function;

public class V2FunctionVar extends AV2Value {
	IV2Function func;
	V2Runtime runtime;
	AV2Value[] args = new AV2Value[0];

	public V2FunctionVar(V2Runtime runtime, AV2Compiler compiler, String name, String[] nonprocessedargs) throws CompileException {
		this.runtime = runtime;
		this.func = runtime.functionHandler.getFunction(name);
		this.args = new AV2Value[nonprocessedargs.length];
		
		for (int i = 0;i<nonprocessedargs.length;i++) this.args[i] = compiler.getV2Value(runtime, nonprocessedargs[i]);
	}
	public V2FunctionVar(V2Runtime runtime, String name, AV2Value[] args) {
		this.runtime = runtime;
		this.func = runtime.functionHandler.getFunction(name);
		this.args = args;
	}

	@Override
	public Object get() throws CompileException {
		return func.execute(runtime, value, args);
	}
	
}
