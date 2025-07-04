package dev.ngspace.hudder.v2runtime.runtime_elements;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.abstractions.ATextCompiler.CharPosition;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.main.config.HudderConfig;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class WhileV2RuntimeElement extends AV2RuntimeElement {
	
	private AV2Value condition;
	private CharPosition charPosition;

	public WhileV2RuntimeElement(HudderConfig info, String condition, String cmds, AV2Compiler compiler, V2Runtime runtime,
			CharPosition charPosition, String filename) throws CompileException {
		this.charPosition = charPosition;
		this.condition = compiler.getV2Value(runtime, condition, charPosition.line, charPosition.charpos);
		this.nestedRuntime = compiler.buildRuntime(info, cmds, new CharPosition(charPosition.line, 1), filename, runtime);
	}
	
	@Override public boolean execute(CompileState meta, StringBuilder builder) throws CompileException {
		short s=0;
		while (condition.asBoolean()) {
			s++;
			if (s==Short.MAX_VALUE&&Hudder.IS_DEBUG) throw new CompileException("Max while loop reached: " + s, charPosition.line, charPosition.charpos);
			CompileState res = nestedRuntime.execute();
			meta.combineWithResult(res.toResult(), false);
			if (res.hasBroken) break;
			if (res.hasReturned) meta.setReturnValue(res.returnValue);
		}
		return true;
	}
}
