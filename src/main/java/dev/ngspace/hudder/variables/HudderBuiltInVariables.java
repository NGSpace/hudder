package dev.ngspace.hudder.variables;

import static dev.ngspace.hudder.api.variableregistry.VariableTypes.BOOLEAN;
import static dev.ngspace.hudder.api.variableregistry.VariableTypes.NUMBER;
import static dev.ngspace.hudder.api.variableregistry.VariableTypes.STRING;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.variableregistry.DataVariable;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.api.variableregistry.VariableTypes;
import dev.ngspace.hudder.variables.advanced.EffectData;
import dev.ngspace.hudder.variables.data.ClientData;
import dev.ngspace.hudder.variables.data.ComputerData;
import dev.ngspace.hudder.variables.data.PlayerData;
import dev.ngspace.hudder.variables.data.WorldData;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;

public class HudderBuiltInVariables {
	protected HudderBuiltInVariables() {}
	
	public static void registerVariables() {
		
		DataVariableRegistry.registerVariable(new EffectData(), "active_effects");
		
		ComputerData.registerVariables();
		PlayerData.registerVariables();
		ClientData.registerVariables();
		WorldData.registerVariables();
		registerMiscVariables();
	}

	public static void register(DataVariable<?> variable, VariableTypes.Type<?> type, String... names) {
		DataVariableRegistry.registerVariable(variable, type, names);
	}
	
	private static void registerMiscVariables() {
		/* Hudder */
		
		// Booleans
		register(k->true, BOOLEAN, "enabled"); // duh
		register(k->Hudder.config.shadow, BOOLEAN, "shadow");
		register(k->Hudder.config.showInF3, BOOLEAN, "showinf3");
		register(k->true, BOOLEAN, "javascriptenabled"); // compatibility
		register(k->Hudder.config.unsafeoperations, BOOLEAN, "unsafeoperations");
		register(k->Hudder.config.globalVariablesEnabled, BOOLEAN, "globalvariablesenabled");
		register(k->Hudder.config.background, BOOLEAN, "background");
		register(k->Hudder.config.removegui, BOOLEAN, "removegui");
		register(k->Hudder.config.limitrate, BOOLEAN, "limitrate");
		
		// Strings
		register(k->Hudder.config.getCompilerName(), STRING, "compilertype");
		register(k->Hudder.config.mainfile, STRING, "mainfile");
		register(k->Hudder.HUDDER_VERSION, STRING, "hudder_version");
		
		// Numbers
		register(k->Hudder.config.scale, NUMBER, "scale");
		register(k->Hudder.config.color, NUMBER, "color");
		register(k->Hudder.config.yoffset, NUMBER, "yoffset");
		register(k->Hudder.config.xoffset, NUMBER, "xoffset");
		register(k->Hudder.config.lineHeight, NUMBER, "lineheight");
		register(k->Hudder.config.methodBuffer, NUMBER, "methodbuffer");
		register(k->Hudder.config.backgroundcolor, NUMBER, "backgroundcolor");

		/* Constants */
		
//		register(k->"unset", STRING, "unset");

		register(k->Minecraft.getInstance().getVersionType(), STRING, "version_type");
		register(k->SharedConstants.getCurrentVersion().id(), STRING, "game_version");

		register(k->0xFF663399, NUMBER, "rebeccapurple");

	}
}