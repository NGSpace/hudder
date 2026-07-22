package dev.ngspace.hudder.hudpacks;

import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableFunction;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.Binder;
import dev.ngspace.hudder.compilers.HudPackCompiler;
import dev.ngspace.hudder.compilers.utils.javascript.JavaScriptEngine;

public class HudPackEngineManager {
	
	private HudPackCompiler compiler;
	private HudPack hudpack;

	public HudPackEngineManager(HudPackCompiler compiler, HudPack hudpack) {
		this.compiler = compiler;
		this.hudpack = hudpack;
	}
	
	private Map<String, JavaScriptEngine> engines = new HashMap<String, JavaScriptEngine>();
	
	public JavaScriptEngine getOrCreateEngine(String hud, String point_code) {
		if (!engines.containsKey(hud)) {
			var engine = new JavaScriptEngine();
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
			engine.bindFunction(e->hudpack.getSettingValue(e[0].asString()), "getHudSetting");
			if (hudpack.format_version>1) {
				engine.bindFunction(e->hudpack.entries.get(e[0].asString()), "readBinaryHudpackFile");
				engine.bindFunction(e->new String(hudpack.entries.get(e[0].asString())), "readHudpackFile");
			}
			engines.put(hud, engine);
			engine.evaluateCode(point_code, hud);
		}
		return engines.get(hud);
	}
}
