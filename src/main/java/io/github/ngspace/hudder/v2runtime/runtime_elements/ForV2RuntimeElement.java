package io.github.ngspace.hudder.v2runtime.runtime_elements;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler.CharPosition;
import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.main.config.HudderConfig;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

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
				if (res.hasReturned) compileState.setReturnValue(res.returnValue);
				if (res.hasBroken) break;
			}
		}
		return true;
	}
}