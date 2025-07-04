package dev.ngspace.hudder.v2runtime.runtime_elements;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.abstractions.ATextCompiler.CharPosition;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.main.config.HudderConfig;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class IfV2RuntimeElement extends AV2RuntimeElement {

	private AV2Value condition;

	public IfV2RuntimeElement(HudderConfig info, String condition, String cmds, AV2Compiler compiler, V2Runtime runtime,
			CharPosition charPosition, String filename) throws CompileException {
		this.nestedRuntime = compiler.buildRuntime(info, cmds, new CharPosition(charPosition.line, 1), filename, runtime);
		this.condition = compiler.getV2Value(nestedRuntime, condition, charPosition.line, charPosition.charpos);
	}
	
	@Override public boolean execute(CompileState meta, StringBuilder builder) throws CompileException {
		if (condition.asBoolean()) {
			CompileState res = nestedRuntime.execute();
			meta.combineWithResult(res.toResult(), false);
			if (res.hasReturned) meta.setReturnValue(res.returnValue);
			if (res.hasBroken) return false;
		}
		return true;
	}
}
