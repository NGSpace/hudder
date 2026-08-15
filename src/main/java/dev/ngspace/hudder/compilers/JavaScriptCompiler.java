package dev.ngspace.hudder.compilers;

import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.compilers.abstractions.AScriptingLanguageCompiler;
import dev.ngspace.hudder.compilers.abstractions.IScriptingLanguageEngine;
import dev.ngspace.hudder.compilers.utils.javascript.JavaScriptEngine;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;

public class JavaScriptCompiler extends AScriptingLanguageCompiler {

	@Override protected IScriptingLanguageEngine createLangEngine() throws CompileException {
		JavaScriptEngine engine = new JavaScriptEngine(elms, this);
		FunctionAndConsumerAPI api = FunctionAndConsumerAPI.getInstance();
		api.applyFunctionsAndConsumers(engine);
		return engine;
	}

	@Override
	public boolean setupHudSettings(NGSMCConfigCategory hudsettings) {
		return false;
	}
	
	@Override
	public String[] getSupportedFileFormats() {
		return new String[] {"js"};
	}
}
