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
import net.minecraft.client.ClientBrandRetriever;

public class HudderBuiltInVariables {
	protected HudderBuiltInVariables() {}
	
	
//	private static final Comparator<PlayerScoreEntry> SCORE_DISPLAY_ORDER = Comparator.comparing(PlayerScoreEntry::value)
//			.reversed()
//			.thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);
	
	public static void registerVariables() {
		
		DataVariableRegistry.registerVariable(new EffectData(), "active_effects");
		
		ComputerData.registerVariables();
		PlayerData.registerVariables();
		ClientData.registerVariables();
		WorldData.registerVariables();
		registerMiscVariables();
		
		
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

	public static void register(DataVariable<?> variable, VariableTypes.Type<?> type, String... names) {
		DataVariableRegistry.registerVariable(variable, type, names);
	}
	
	private static void registerMiscVariables() {
		/* Hudder */
		
		// Booleans
		register(_->true, BOOLEAN, "enabled"); // duh
		register(_->Hudder.config.shadow(), BOOLEAN, "shadow");
		register(_->Hudder.config.showInF3(), BOOLEAN, "showinf3");
		register(_->true, BOOLEAN, "javascriptenabled"); // compatibility
		register(_->Hudder.config.unsafeoperations(), BOOLEAN, "unsafeoperations");
		register(_->Hudder.config.globalVariablesEnabled(), BOOLEAN, "globalvariablesenabled");
		register(_->Hudder.config.background(), BOOLEAN, "background");
		register(_->Hudder.config.removegui(), BOOLEAN, "removegui");
		register(_->Hudder.config.removeeffects(), BOOLEAN, "removeeffects");
		register(_->Hudder.config.limitrate(), BOOLEAN, "limitrate");
		register(_->Hudder.config.disableHudpackVersionCheck(), BOOLEAN, "disable_hudpack_version_check");
		register(_->Hudder.config.disableWarnings(), BOOLEAN, "disable_warnings");
		
		// Strings
		register(_->Hudder.config.compilerName(), STRING, "compilertype");
		register(_->Hudder.config.mainfile(), STRING, "mainfile");
		register(_->Hudder.HUDDER_VERSION, STRING, "hudder_version");
		
		// Numbers
		register(_->Hudder.config.scale(), NUMBER, "scale");
		register(_->Hudder.config.color(), NUMBER, "color");
		register(_->Hudder.config.yoffsetTop(), NUMBER, "yoffset_top", "yoffset");
		register(_->Hudder.config.yoffsetBottom(), NUMBER, "yoffset_bottom");
		register(_->Hudder.config.xoffsetLeft(), NUMBER, "xoffset_left", "xoffset");
		register(_->Hudder.config.xoffsetRight(), NUMBER, "xoffset_right");
		register(_->Hudder.config.lineHeight(), NUMBER, "lineheight");
		register(_->Hudder.config.methodBuffer(), NUMBER, "methodbuffer");
		register(_->Hudder.config.backgroundcolor(), NUMBER, "backgroundcolor");

		/* Constants */
		
//		register(k->"unset", STRING, "unset");

		register(_->ClientBrandRetriever.getClientModName(), STRING, "version_type");
		register(_->SharedConstants.getCurrentVersion().id(), STRING, "game_version");

		register(_->0xFF663399, NUMBER, "rebeccapurple");

	}
}