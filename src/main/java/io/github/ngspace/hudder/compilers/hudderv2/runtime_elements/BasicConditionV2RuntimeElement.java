package io.github.ngspace.hudder.compilers.hudderv2.runtime_elements;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.CompileResult;
import io.github.ngspace.hudder.compilers.TextCompiler;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.CompileState;

//What a name...
public class BasicConditionV2RuntimeElement extends AV2RuntimeElement{

	final String[] condArgs;
	final TextCompiler compiler;
	final ConfigInfo info;
	public BasicConditionV2RuntimeElement(String[] condArgs, TextCompiler compiler, ConfigInfo info) {
		this.condArgs = condArgs;
		this.compiler = compiler;
		this.info = info;
	}
	
	@Override public void execute(CompileState meta, StringBuilder builder) throws CompileException {
		CompileResult res = compiler.solveCondition(info,condArgs);
		builder.append(res.TopLeftText);
	}
}
