package io.github.ngspace.hudder.v2runtime.runtime_elements;

import java.util.Arrays;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.methods.IMethod;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.V2Value;

public class MethodV2RuntimeElement extends AV2RuntimeElement {

	private V2Value[] values = {};
	private String type;
	private AV2Compiler compiler;
	private ConfigInfo info;
	private IMethod method;

	public MethodV2RuntimeElement(String[] args, AV2Compiler compiler, ConfigInfo info, V2Runtime runtime) throws CompileException {
		this.compiler = compiler;
		this.info = info;
		type = args[0];
		for (int i = 1;i<args.length;i++) {
			values = Arrays.copyOf(values, values.length+1);
			values[values.length-1] = compiler.getV2Value(args[i]);
		}
		method = runtime.methodHandler.getMethodFromName(type);
	}
	@Override public void execute(CompileState meta, StringBuilder builder) throws CompileException {
		method.invoke(info, meta, compiler, type, values);
	}
}
