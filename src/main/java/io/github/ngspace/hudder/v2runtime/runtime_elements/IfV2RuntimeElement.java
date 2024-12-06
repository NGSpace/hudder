package io.github.ngspace.hudder.v2runtime.runtime_elements;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler.CharPosition;
import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.hudder.config.HudderConfig;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class IfV2RuntimeElement extends AV2RuntimeElement {

	private AV2Value condition;
	private V2Runtime compiledRuntime;

	public IfV2RuntimeElement(HudderConfig info, String condition, String cmds, AV2Compiler compiler, V2Runtime runtime, CharPosition charPosition, String filename) throws CompileException {
		this.condition = compiler.getV2Value(runtime, condition, charPosition.line, charPosition.charpos);
		this.compiledRuntime = compiler.buildRuntime(info, cmds, new CharPosition(charPosition.line, 1), filename);
	}
	
	@Override
	public boolean execute(CompileState meta, StringBuilder builder) throws CompileException {
		if(condition.asBoolean()) {
			CompileState cs = compiledRuntime.execute();
			meta.combineWithResult(cs.toResult(), false);
			return !cs.hasBroken;
		}
		return true;
	}
	
}
