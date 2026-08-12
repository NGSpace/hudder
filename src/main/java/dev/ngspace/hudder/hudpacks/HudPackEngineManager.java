package dev.ngspace.hudder.hudpacks;

import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.PositionedBinder;
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
			FunctionAndConsumerAPI.getInstance().applyFunctionsAndConsumers(new PositionedBinder() {
				@Override
				public void bindFunction(BindablePositionedFunction c, String... n) {
					engine.bindFunction((p,e)->c.invoke(compiler.elms, compiler, p, e), n);
				}
				
				@Override
				public void bindConsumer(BindablePositionedConsumer c, String... n) {
					engine.bindConsumer((p,e)->c.invoke(compiler.elms, compiler, p, e), n);
				}
			});
			engine.bindFunction((_,e)->hudpack.getSettingValue(e[0].asString()), "getHudSetting");
			if (hudpack.format_version>1) {
				engine.bindFunction((_,e)->hudpack.entries.get(e[0].asString()), "readBinaryHudpackFile");
				engine.bindFunction((_,e)->new String(hudpack.entries.get(e[0].asString())), "readHudpackFile");
			}
			engines.put(hud, engine);
			engine.evaluateCode(point_code, hud);
		}
		return engines.get(hud);
	}
}
