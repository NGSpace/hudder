package io.github.ngspace.hudder.compilers.v2runtime.runtime_elements;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.v2runtime.AV2Compiler;
import io.github.ngspace.hudder.compilers.v2runtime.values.V2Value;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.CompileState;

public class WhileV2RuntimeElement extends AV2RuntimeElement {
	
	private V2Value condition;
	private ConfigInfo info;
	private String cmds;
	private AV2Compiler compiler;

	public WhileV2RuntimeElement(ConfigInfo info, String condition, String cmds, AV2Compiler compiler) {
		this.condition = compiler.getV2Value(condition);
		this.cmds = cmds;
		this.info = info;
		this.compiler = compiler;
	}
	
	@Override
	public void execute(CompileState meta, StringBuilder builder) throws CompileException {
		short s = 32767;
		while(condition.asBoolean()) {
			s--;
			if (s==0) throw new CompileException("While loop limited to 32767");
			meta.combineWithResult(compiler.compile(info, cmds), false);
		}
	}
	
}
