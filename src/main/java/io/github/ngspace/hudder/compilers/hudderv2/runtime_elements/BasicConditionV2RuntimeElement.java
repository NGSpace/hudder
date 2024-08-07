package io.github.ngspace.hudder.compilers.hudderv2.runtime_elements;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.CompileResult;
import io.github.ngspace.hudder.compilers.hudderv2.V2Runtime;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.CompileState;

//What a name...
public class BasicConditionV2RuntimeElement extends AV2RuntimeElement{

	final String[] condArgs;
	final V2Runtime runtime;
	final ConfigInfo info;
	public BasicConditionV2RuntimeElement(String[] condArgs, V2Runtime runtime, ConfigInfo info) {
		this.condArgs = condArgs;
		this.runtime = runtime;
		this.info = info;
	}
	
	@Override public void execute(CompileState meta, StringBuilder builder) throws CompileException {
		CompileResult res = runtime.compiler.solveCondition(info,condArgs);
		builder.append(res.TopLeftText);
	}
}
