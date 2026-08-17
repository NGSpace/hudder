package dev.ngspace.hudder.hudpacks;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.compilers.HudPackCompiler;
import dev.ngspace.hudder.compilers.utils.javascript.JavaScriptEngine;
import dev.ngspace.hudder.config.HudderConfig;

public class HudPackEngineManager implements Closeable {
	
	private HudPackCompiler compiler;
	private HudPack hudpack;

	public HudPackEngineManager(HudPackCompiler compiler, HudPack hudpack) {
		this.compiler = compiler;
		this.hudpack = hudpack;
	}
	
	private Map<String, JavaScriptEngine> engines = new HashMap<String, JavaScriptEngine>();
	
	public JavaScriptEngine getOrCreateEngine(String hud, HudderConfig config, String point_code) {
		if (!engines.containsKey(hud)) {
			JavaScriptEngine engine = new JavaScriptEngine(compiler.elms, compiler, config);
			FunctionAndConsumerAPI.getInstance().applyFunctionsAndConsumers(engine);
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

	public void close() throws IOException {
		for (var engine : engines.values())
			engine.close();
	}
}
