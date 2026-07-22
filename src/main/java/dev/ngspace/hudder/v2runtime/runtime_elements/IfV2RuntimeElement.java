package dev.ngspace.hudder.v2runtime.runtime_elements;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

/**
 * @deprecated Use {@link IfElseV2RuntimeElement}
 */
@Deprecated(since = "10.1.0", forRemoval = false)
public class IfV2RuntimeElement extends AV2RuntimeElement {

	private AV2Value condition;

	@Deprecated(since = "10.1.0", forRemoval = false)
	public IfV2RuntimeElement(HudderConfig info, String condition, String cmds, AV2Compiler compiler, V2Runtime runtime,
			TextPos charPosition, String filename) throws CompileException, ExecutionException {
		this.nestedRuntimes = new V2Runtime[] {compiler.buildRuntime(info, cmds,
				new TextPos(charPosition.line(), 1), filename, runtime)};
		this.condition = compiler.getV2Value(nestedRuntimes[0], condition, charPosition.line(), charPosition.column());
	}
	
	@Deprecated(since = "10.1.0", forRemoval = false)
	@Override public boolean execute(CompileState meta, StringBuilder builder) throws ExecutionException {
		if (condition.asBoolean()) {
			CompileState res = nestedRuntimes[0].execute();
			meta.combineWithResult(res.toResult(), false);
			if (res.hasReturned) meta.setReturnValue(res.returnValue);
			if (res.hasBroken) return false;
		}
		return true;
	}
}
