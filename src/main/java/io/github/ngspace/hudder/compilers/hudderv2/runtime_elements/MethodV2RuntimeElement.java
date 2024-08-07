package io.github.ngspace.hudder.compilers.hudderv2.runtime_elements;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
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
