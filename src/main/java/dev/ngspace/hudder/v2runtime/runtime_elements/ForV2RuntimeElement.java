package dev.ngspace.hudder.v2runtime.runtime_elements;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.abstractions.ATextCompiler.CharPosition;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.main.config.HudderConfig;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class ForV2RuntimeElement extends AV2RuntimeElement {

	private String variablename;
	private AV2Value condition;
	
	public ForV2RuntimeElement(HudderConfig info, String variablename, String value, String instructions,
			AV2Compiler compiler, V2Runtime parentRuntime, CharPosition charPosition, String filename)
					throws CompileException {
		this.variablename = variablename;
		this.condition = compiler.getV2Value(parentRuntime, value, charPosition.line, charPosition.charpos);
		this.nestedRuntime = compiler.buildRuntime(info, instructions, new CharPosition(charPosition.line, 1),
				filename, parentRuntime);
	}
	
	@Override
	public boolean execute(CompileState compileState, StringBuilder builder) throws CompileException {
		if (condition.get() instanceof Iterable<?> iterable) {
			for (Object val : iterable) {
				nestedRuntime.putScoped(variablename, val);
				CompileState res = nestedRuntime.execute();
				compileState.combineWithResult(res.toResult(), false);
				if (res.hasBroken) break;
				if (res.hasReturned) compileState.setReturnValue(res.returnValue);
			}
		}
		return true;
	}
}