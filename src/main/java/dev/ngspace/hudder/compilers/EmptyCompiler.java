package dev.ngspace.hudder.compilers;

import dev.ngspace.hudder.compilers.abstractions.ATextCompiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.main.config.HudderConfig;

public class EmptyCompiler extends ATextCompiler {
	@Override
	public HudInformation compile(HudderConfig info, String text, String filename) throws CompileException {return HudInformation.of(text);}

	@Override public Object getVariable(String key) throws CompileException {return null;}
}
