package io.github.ngspace.hudder.compilers;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.compilers.utils.IScriptingLanguageEngine;
import io.github.ngspace.hudder.compilers.utils.JavaScriptEngineWrapper;
import io.github.ngspace.hudder.config.ConfigInfo;


public class JavaScriptCompiler extends AScriptingLanguageCompiler {
	
	@Override
	public HudInformation compile(ConfigInfo info, String text, String filename) throws CompileException {
		if (!Hudder.config.javascript) throw new CompileException("JavaScript is disabled!",-1,-1);
		return super.compile(info, text, filename);
	}
	
	protected IScriptingLanguageEngine createLangEngine() throws CompileException {
		return new JavaScriptEngineWrapper();
	}
}