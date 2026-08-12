package dev.ngspace.hudder.compilers;

import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.PositionedBinder;
import dev.ngspace.hudder.compilers.abstractions.AScriptingLanguageCompiler;
import dev.ngspace.hudder.compilers.abstractions.IScriptingLanguageEngine;
import dev.ngspace.hudder.compilers.utils.javascript.JavaScriptEngine;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;

public class JavaScriptCompiler extends AScriptingLanguageCompiler {

	@Override protected IScriptingLanguageEngine createLangEngine() throws CompileException {
		JavaScriptEngine engine = new JavaScriptEngine();
		FunctionAndConsumerAPI api = FunctionAndConsumerAPI.getInstance();
		var compiler = this;
		api.applyFunctionsAndConsumers(new PositionedBinder() {
			@Override
			public void bindFunction(BindablePositionedFunction c, String... n) {
				engine.bindFunction((p,e)->c.invoke(elms, compiler, p, e), n);
			}
			
			@Override
			public void bindConsumer(BindablePositionedConsumer c, String... n) {
				engine.bindConsumer((p,e)->c.invoke(elms, compiler, p, e), n);
			}
		});
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
