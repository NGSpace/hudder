package dev.ngspace.hudder.variables;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.variableregistry.DataVariable;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.variables.advanced.EffectData;
import dev.ngspace.hudder.variables.data.ClientData;
import dev.ngspace.hudder.variables.data.ComputerData;
import dev.ngspace.hudder.variables.data.PlayerData;
import dev.ngspace.hudder.variables.data.WorldData;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;

public class HudderBuiltInVariables {
	protected HudderBuiltInVariables() {}
	
	
//	private static final Comparator<PlayerScoreEntry> SCORE_DISPLAY_ORDER = Comparator.comparing(PlayerScoreEntry::value)
//			.reversed()
//			.thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);
	
	public static void registerVariables(HudderConfig config) {
		
		registerObject(new EffectData(), "active_effects");
		
		ComputerData.registerVariables();
		PlayerData.registerVariables();
		ClientData.registerVariables();
		WorldData.registerVariables();
		registerMiscVariables(config);
		
		
		// Maybe later...
//		register(_->{
//			var ins = Minecraft.getInstance();
//			
//			Scoreboard scoreboard = ins.level.getScoreboard();
//			
//			var obj = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
//			NumberFormat objectiveScoreFormat = obj.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);
//			
//			@Environment(EnvType.CLIENT)
//			record DisplayEntry(Component name, Component score, int scoreWidth) {
//			}
//
//			DisplayEntry[] entriesToDisplay = scoreboard.listPlayerScores(obj)
//				.stream()
//				.filter(input -> !input.isHidden())
//				.sorted(SCORE_DISPLAY_ORDER)
//				.limit(15L)
//				.map(score -> {
//					PlayerTeam team = scoreboard.getPlayersTeam(score.owner());
//					Component ownerName = score.ownerName();
//					Component name = PlayerTeam.formatNameForTeam(team, ownerName);
//					Component scoreString = score.formatValue(objectiveScoreFormat);
//					int scoreWidth = ins.font.width(scoreString);
//					return new DisplayEntry(name, scoreString, scoreWidth);
//				})
//				.toArray(DisplayEntry[]::new);
//			
//			return entriesToDisplay[3].name();
//		}, VariableTypes.OBJECT, "scoreboard");
	}

	public static void registerBoolean(DataVariable<Boolean> variable, String... names) {
		DataVariableRegistry.registerBooleanVariable(variable, names);
	}

	public static void registerString(DataVariable<String> variable, String... names) {
		DataVariableRegistry.registerStringVariable(variable, names);
	}

	public static void registerNumber(DataVariable<Number> variable, String... names) {
		DataVariableRegistry.registerNumberVariable(variable, names);
	}

	public static void registerObject(DataVariable<Object> variable, String... names) {
		DataVariableRegistry.registerObjectVariable(variable, names);
	}
	
	private static void registerMiscVariables(HudderConfig config) {
		/* Hudder */
		
		// Booleans
		registerBoolean(_->true, "enabled"); // duh
		registerBoolean(_->config.shadow(), "shadow");
		registerBoolean(_->config.showInF3(), "showinf3");
		registerBoolean(_->true, "javascriptenabled"); // compatibility
		registerBoolean(_->config.unsafeoperations(), "unsafeoperations");
		registerBoolean(_->config.globalVariablesEnabled(), "globalvariablesenabled");
		registerBoolean(_->config.background(), "background");
		registerBoolean(_->config.removegui(), "removegui");
		registerBoolean(_->config.removeeffects(), "removeeffects");
		registerBoolean(_->config.limitrate(), "limitrate");
		registerBoolean(_->config.disableHudpackVersionCheck(), "disable_hudpack_version_check");
		registerBoolean(_->config.disableWarnings(), "disable_warnings");
		registerBoolean(_->config.compilationManager.isFirstRunSinceCacheClear, "first_execution");
		
		// Strings
		registerString(_->config.compilerId(), "compilertype");
		registerString(_->config.mainfile(), "mainfile");
		registerString(_->Hudder.HUDDER_VERSION, "hudder_version");
		
		// Numbers
		registerNumber(_->config.scale(), "scale");
		registerNumber(_->config.color(), "color");
		registerNumber(_->config.yoffsetTop(), "yoffset_top", "yoffset");
		registerNumber(_->config.yoffsetBottom(), "yoffset_bottom");
		registerNumber(_->config.xoffsetLeft(), "xoffset_left", "xoffset");
		registerNumber(_->config.xoffsetRight(), "xoffset_right");
		registerNumber(_->config.lineHeight(), "lineheight");
		registerNumber(_->config.methodBuffer(), "methodbuffer");
		registerNumber(_->config.backgroundcolor(), "backgroundcolor");

		/* Constants */
		registerString(_->ClientBrandRetriever.getClientModName(), "version_type");
		registerString(_->SharedConstants.getCurrentVersion().id(), "game_version");

		registerNumber(_->0xFF663399, "rebeccapurple");

	}
}