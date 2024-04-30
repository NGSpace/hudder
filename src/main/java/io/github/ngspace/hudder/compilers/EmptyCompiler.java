package io.github.ngspace.hudder.compilers;

import io.github.ngspace.hudder.config.ConfigInfo;

public class EmptyCompiler extends AVarTextCompiler {
	@Override
	public CompileResult compile(ConfigInfo info, String text) throws CompileException {return CompileResult.of(text);}
}
