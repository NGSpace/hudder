package dev.ngspace.hudder.hudpacks;

import java.io.IOException;

import dev.ngspace.hudder.compilers.HudPackCompiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI.BindableConsumer;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI.BindableFunction;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI.Binder;
import dev.ngspace.hudder.compilers.utils.javascript.JavaScriptEngine;

import static dev.ngspace.hudder.hudpacks.HudPackHudState.*;

public class HudPackPoint {
	
	
	public JavaScriptEngine engine;
	public String path;
	public HudPackPointConfig config;
	public String point_code;

	public HudPackPoint(HudPackPointConfig config, String point_code, HudPackCompiler compiler) {
		this.config = config;
		this.point_code = point_code;
		
		this.engine = new JavaScriptEngine();
		FunctionAndConsumerAPI.getInstance().applyFunctionsAndConsumers(new Binder() {
			@Override
			public void bindFunction(BindableFunction c, String... n) {
				engine.bindFunction(e->c.invoke(compiler.elms, compiler, e), n);
			}
			
			@Override
			public void bindConsumer(BindableConsumer c, String... n) {
				engine.bindConsumer(e->c.invoke(compiler.elms, compiler, e), n);
			}
		});
		engine.evaluateCode(point_code, config.path());
	}
	
	public void execute(HudPackHudState state) throws IOException, CompileException {
		switch (config.type()) {
			case TOPLEFT, BOTTOMLEFT, TOPRIGHT, BOTTOMRIGHT: 
				state.addString(engine.callFunction(config.entry_function()).asString(), config.type());
				break;
			default: 
				engine.callFunction(config.entry_function());
				break;
		}
	}
}
