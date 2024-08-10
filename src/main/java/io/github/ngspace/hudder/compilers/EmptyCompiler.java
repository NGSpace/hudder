package io.github.ngspace.hudder.compilers;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.config.ConfigInfo;

public class EmptyCompiler extends ATextCompiler {
	@Override
	public CompileResult compile(ConfigInfo info, String text) throws CompileException {return CompileResult.of(text);}

	@Override public Object getVariable(String key) throws CompileException {return "";}

	@Override
	public boolean conditionCheck(String condition) {
		// TODO Auto-generated method stub
		return false;
	}
}
