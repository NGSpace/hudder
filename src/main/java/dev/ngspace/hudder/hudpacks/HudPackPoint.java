package dev.ngspace.hudder.hudpacks;

import static dev.ngspace.hudder.hudpacks.HudPackHudState.BOTTOMLEFT;
import static dev.ngspace.hudder.hudpacks.HudPackHudState.BOTTOMRIGHT;
import static dev.ngspace.hudder.hudpacks.HudPackHudState.TOPLEFT;
import static dev.ngspace.hudder.hudpacks.HudPackHudState.TOPRIGHT;

import java.io.IOException;
import java.util.Arrays;

import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.compilers.utils.javascript.JavaScriptEngine;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class HudPackPoint {
	
	
	public JavaScriptEngine engine;
	public String path;
	public HudPackPointConfig config;
	public AV2Value[] conditions;

	public HudPackPoint(HudPackPointConfig config, JavaScriptEngine engine) {
		this.config = config;
		this.engine = engine;
		this.conditions = config.conditions()==null ? null : Arrays.stream(config.conditions())
				.map(DataVariableRegistry::getBoolean)
				.toArray(AV2Value[]::new);
	}
	
	public void execute(HudPackHudState state) throws IOException, ExecutionException {
		switch (config.type()) {
			case TOPLEFT, BOTTOMLEFT, TOPRIGHT, BOTTOMRIGHT: 
				state.addString(engine.callFunction(config.entry_function()).asString(), config.type());
				break;
			case "mute", "elements": 
				engine.callFunction(config.entry_function());
				break;
			default:
				throw new IllegalArgumentException("Illegal point type: \"" + config.type() + '"');
		}
	}
}
