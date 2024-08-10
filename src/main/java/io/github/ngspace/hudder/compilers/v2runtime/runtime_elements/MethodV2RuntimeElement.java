package io.github.ngspace.hudder.compilers.v2runtime.runtime_elements;

import io.github.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.CompileState;
import io.github.ngspace.hudder.meta.MethodHandler;

public class MethodV2RuntimeElement extends AV2RuntimeElement {
	
	private String[] args;
	private AVarTextCompiler compiler;
	private ConfigInfo info;
	
	public final MethodHandler metacomp = new MethodHandler();

	public MethodV2RuntimeElement(String[] metabuilder, AVarTextCompiler compiler, ConfigInfo info) {
		this.args = metabuilder;
		this.compiler = compiler;
		this.info = info;
	}

	@Override public void execute(CompileState meta, StringBuilder builder) throws CompileException {
		metacomp.execute(info, meta, compiler, args);
	}
}
