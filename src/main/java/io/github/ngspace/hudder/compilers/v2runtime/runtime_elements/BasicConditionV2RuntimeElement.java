package io.github.ngspace.hudder.compilers.v2runtime.runtime_elements;

import io.github.ngspace.hudder.compilers.abstractions.AConditionCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.CompileState;

//What a name...
public class BasicConditionV2RuntimeElement extends AV2RuntimeElement{

	final String[] condArgs;
	final AConditionCompiler compiler;
	final ConfigInfo info;
	public BasicConditionV2RuntimeElement(String[] condArgs, AConditionCompiler compiler, ConfigInfo info) {
		this.condArgs = condArgs;
		this.compiler = compiler;
		this.info = info;
	}
	
	@Override public void execute(CompileState meta, StringBuilder builder) throws CompileException {
		CompileResult res = compiler.solveCondition(info,condArgs);
		builder.append(res.TopLeftText);
	}
}
