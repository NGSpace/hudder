package io.github.ngspace.hudder.compilers.v2runtime.runtime_elements;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.CompileState;

public class WhileV2RuntimeElement extends AV2RuntimeElement {
	
	private String condition;
	private ConfigInfo info;
	private String cmds;
	private ATextCompiler compiler;

	public WhileV2RuntimeElement(ConfigInfo info, String condition, String cmds, ATextCompiler compiler) {
		this.condition = condition;
		this.cmds = cmds;
		this.info = info;
		this.compiler = compiler;
	}
	
	@Override
	public void execute(CompileState meta, StringBuilder builder) throws CompileException {
		short s = 32767;
		while(compiler.conditionCheck(condition)) {
			s--;
			if (s==0) throw new CompileException("While loop limited to 32767");
			meta.combineWithResult(compiler.compile(info, cmds), false);
		}
	}
	
}
