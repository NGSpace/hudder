package io.github.ngspace.hudder.v2runtime.values;

import java.util.Arrays;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.functions.AV2Function;

public class V2FunctionVar extends AV2Value {
	AV2Function func;
	V2Runtime runtime;
	AV2Value[] args = new AV2Value[0];

	public V2FunctionVar(V2Runtime runtime, AV2Compiler compiler, String name, String[] nonprocessedargs) throws CompileException {
		this.runtime = runtime;
		this.func = runtime.functionHandler.getFunction(name);
		for (int i = 1;i<nonprocessedargs.length;i++) {
			args = Arrays.copyOf(args, args.length+1);
			args[args.length-1] = compiler.getV2Value(runtime, nonprocessedargs[i]);
		}
	}
	public V2FunctionVar(V2Runtime runtime, String name, AV2Value[] args) {
		this.runtime = runtime;
		this.func = runtime.functionHandler.getFunction(name);
		this.args = args;
	}

	@Override
	public Object get() throws CompileException {
		return func.execute(runtime, value, new AV2Value[0]);
	}
	
}
