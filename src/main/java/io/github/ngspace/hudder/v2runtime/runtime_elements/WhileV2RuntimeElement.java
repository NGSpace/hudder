package io.github.ngspace.hudder.v2runtime.runtime_elements;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler.CharPosition;
import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.main.config.HudderConfig;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class WhileV2RuntimeElement extends AV2RuntimeElement {
	
	private AV2Value condition;

	public WhileV2RuntimeElement(HudderConfig info, String condition, String cmds, AV2Compiler compiler, V2Runtime runtime,
			CharPosition charPosition, String filename) throws CompileException {
		this.condition = compiler.getV2Value(runtime, condition, charPosition.line, charPosition.charpos);
		this.nestedRuntime = compiler.buildRuntime(info, cmds, new CharPosition(charPosition.line, 1), filename, runtime);
	}
	
	@Override public boolean execute(CompileState meta, StringBuilder builder) throws CompileException {
		while (condition.asBoolean()) {
			CompileState res = nestedRuntime.execute();
			meta.combineWithResult(res.toResult(), false);
			if (res.hasReturned) meta.setReturnValue(res.returnValue);
			
		}
		return true;
	}
}
