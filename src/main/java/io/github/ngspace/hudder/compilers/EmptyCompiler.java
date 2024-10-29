package io.github.ngspace.hudder.compilers;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.config.ConfigInfo;

public class EmptyCompiler extends ATextCompiler {
	@Override
	public HudInformation compile(ConfigInfo info, String text, String filename) throws CompileException {return HudInformation.of(text);}

	@Override public Object getVariable(String key) throws CompileException {return null;}
}
