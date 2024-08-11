package io.github.ngspace.hudder.compilers.v2runtime.runtime_elements;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.v2runtime.values.V2Value;
import io.github.ngspace.hudder.compilers.v2runtime.values.V2Values;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.CompileState;

public class IfV2RuntimeElement extends AV2RuntimeElement {

	private V2Value condition;
	private ConfigInfo info;
	private String cmds;
	private ATextCompiler compiler;

	public IfV2RuntimeElement(ConfigInfo info, String condition, String cmds, AVarTextCompiler compiler) {
		this.condition = V2Values.of(condition, compiler);
		this.cmds = cmds;
		this.info = info;
		this.compiler = compiler;
	}
	
	@Override
	public void execute(CompileState meta, StringBuilder builder) throws CompileException {
		boolean condr = condition.asBoolean();
		if(condr) meta.combineWithResult(compiler.compile(info, cmds), false);
	}
	
}