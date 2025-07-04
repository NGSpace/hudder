package dev.ngspace.hudder.compilers;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AScriptingLanguageCompiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.compilers.utils.IScriptingLanguageEngine;
import dev.ngspace.hudder.compilers.utils.JavaScriptEngine;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI.BindableConsumer;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI.BindableFunction;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI.Binder;
import dev.ngspace.hudder.main.config.HudderConfig;

public class JavaScriptCompiler extends AScriptingLanguageCompiler {

	@Override public HudInformation compile(HudderConfig info, String text, String filename) throws CompileException {
		if (!Hudder.config.javascript) throw new CompileException("JavaScript is disabled!",-1,-1);
		return super.compile(info, text, filename);
	}
	@Override protected IScriptingLanguageEngine createLangEngine() throws CompileException {
		JavaScriptEngine engine = new JavaScriptEngine();
		FunctionAndConsumerAPI api = FunctionAndConsumerAPI.getInstance();
		var compiler = this;
		api.applyFunctionsAndConsumers(new Binder() {
			
			@Override
			public void bindFunction(BindableFunction c, String... n) {
				engine.bindFunction(e->c.invoke(elms, compiler, e), n);
			}
			
			@Override
			public void bindConsumer(BindableConsumer c, String... n) {
				engine.bindConsumer(e->c.invoke(elms, compiler, e), n);
			}
		});
		return engine;
	}
}
