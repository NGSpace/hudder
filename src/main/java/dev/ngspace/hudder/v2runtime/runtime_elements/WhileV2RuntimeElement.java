package dev.ngspace.hudder.v2runtime.runtime_elements;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.utils.CharPosition;
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
		this.nestedRuntime = compiler.buildRuntime(info, cmds, new CharPosition(charPosition.line(), 1), filename, runtime);
		this.condition = compiler.getV2Value(nestedRuntime, condition, charPosition.line(), charPosition.charpos());
	}
	
	@Override public boolean execute(CompileState meta, StringBuilder builder) throws CompileException {
		short s=0;
		while (condition.asBoolean()) {
			if (!Hudder.config.unsafeoperations) {
				s++;
				if (s==Short.MAX_VALUE)
					throw new CompileException("Max while loop reached: " + s, charPosition.line(), charPosition.charpos());
			}
			CompileState res = nestedRuntime.execute();
			meta.combineWithResult(res.toResult(), false);
			if (res.hasReturned) meta.setReturnValue(res.returnValue);
			if (res.hasBroken) break;
		}
		return true;
	}
}
