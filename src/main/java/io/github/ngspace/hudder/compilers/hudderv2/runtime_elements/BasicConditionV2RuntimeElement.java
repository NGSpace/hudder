package io.github.ngspace.hudder.compilers.hudderv2.runtime_elements;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.CompileResult;
import io.github.ngspace.hudder.compilers.hudderv2.HudderV2Runtime;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;

//What a name...
public class BasicConditionV2RuntimeElement extends AV2RuntimeElement{

	final String[] condArgs;
	final HudderV2Runtime runtime;
	final ConfigInfo info;
	public BasicConditionV2RuntimeElement(String[] condArgs, HudderV2Runtime runtime, ConfigInfo info) {
		this.condArgs = condArgs;
		this.runtime = runtime;
		this.info = info;
	}
	
	@Override public void execute(Meta meta, StringBuilder builder) throws CompileException {
		CompileResult res = runtime.compiler.solveCondition(info,condArgs);
		builder.append(res.TopLeftText);
	}
}
