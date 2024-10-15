package io.github.ngspace.hudder.v2runtime.runtime_elements;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class IfV2RuntimeElement extends AV2RuntimeElement {

	private AV2Value condition;
	private ConfigInfo info;
	private String cmds;
	private AV2Compiler compiler;

	public IfV2RuntimeElement(ConfigInfo info, String condition, String cmds, AV2Compiler compiler, V2Runtime runtime) throws CompileException {
		this.condition = compiler.getV2Value(runtime, condition);
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
