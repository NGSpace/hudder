package dev.ngspace.hudder.api.compilers.defaultcompilers;

import dev.ngspace.hudder.api.compilers.abstractions.AScriptingLanguageCompiler;
import dev.ngspace.hudder.api.compilers.abstractions.IScriptingLanguageEngine;
import dev.ngspace.hudder.api.compilers.defaultcompilers.javascript.JavaScriptEngine;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;

public class JavaScriptCompiler extends AScriptingLanguageCompiler {

	public JavaScriptCompiler(HudderConfig config) {
		super(config);
	}

	@Override protected IScriptingLanguageEngine createLangEngine() throws CompileException {
		JavaScriptEngine engine = new JavaScriptEngine(elms, this, config);
		FunctionAndConsumerAPI api = FunctionAndConsumerAPI.getInstance();
		api.applyFunctionsAndConsumers(engine);
		return engine;
	}
	
	@Override
	public String[] getSupportedFileFormats() {
		return new String[] {"js"};
	}
}
