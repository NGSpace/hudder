package dev.ngspace.hudder.variables.data;

import static dev.ngspace.hudder.api.variableregistry.VariableTypes.BOOLEAN;
import static dev.ngspace.hudder.api.variableregistry.VariableTypes.NUMBER;
import static dev.ngspace.hudder.api.variableregistry.VariableTypes.OBJECT;
import static dev.ngspace.hudder.api.variableregistry.VariableTypes.STRING;

import dev.ngspace.hudder.variables.HudderBuiltInVariables;
import dev.ngspace.hudder.variables.advanced.Misc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.dialog.DialogScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class ClientData extends HudderBuiltInVariables {
	static Minecraft ins;
	
	public static void registerVariables() {
		ins = Minecraft.getInstance();
		registerInputVariables();
		registerScreenVariables();
		registerScreenTypeVariables();

		// Camera state
		register(_->ins.gameRenderer.getMainCamera().entity() != ins.player, BOOLEAN, "camera_detached");
		
		// Resource packs
		register(_->ins.getResourcePackRepository().getSelectedPacks().stream()
					.filter(pack->!pack.isRequired())
					.map(t -> t.getTitle().getString())
					.toList(), OBJECT, "selectedresourcepacks");
		register(_->ins.getResourcePackRepository().getSelectedPacks().stream()
					.map(t -> t.getTitle().getString())
					.toList(), OBJECT, "selectedresourcepacks_unfiltered");
	}

	private static void registerInputVariables() {
		// Mouse buttons
		register(_->ins.mouseHandler.isLeftPressed(), BOOLEAN, "mouse_left");
		register(_->ins.mouseHandler.isMiddlePressed(), BOOLEAN, "mouse_middle");
		register(_->ins.mouseHandler.isRightPressed(), BOOLEAN, "mouse_right");

		// Clicks per second
		register(_->Misc.getLeftCPS() + Misc.getRightCPS(), NUMBER, "cps");
		register(_->Misc.getLeftCPS(), NUMBER, "cps_left");
		register(_->Misc.getRightCPS(), NUMBER, "cps_right");
	}

	private static void registerScreenVariables() {
		// Window / GUI
		register(_->ins.getWindow().getGuiScaledWidth(), NUMBER, "width");
		register(_->ins.getWindow().getGuiScaledHeight(), NUMBER, "height");
		register(_->ins.getWindow().getGuiScale(), NUMBER, "guiscale");

		// Open GUI
		register(_->Misc.getScreenType(ins.screen), STRING, "openguitype");

		register(_->(ins.screen == null) ? null : ins.screen.getTitle().getString(), STRING, "openguititle");

		// HUD / debug
		register(_->ins.options.hideGui, BOOLEAN, "hudhidden");
		register(_->ins.getDebugOverlay().showDebugScreen(), BOOLEAN, "showdebug");
		register(_->ins.debugEntries.isOverlayVisible(), BOOLEAN, "f3enabled");
	}
	
	private static void registerScreenTypeVariables() {
		register(_->ins.screen != null, BOOLEAN, "isguiopen");
		register(_->ins.screen instanceof ContainerScreen, BOOLEAN, "ischestopen");
		register(_->ins.screen instanceof CraftingScreen, BOOLEAN, "iscraftingtableopen");
		register(_->ins.screen instanceof ChatScreen, BOOLEAN, "ischatopen");
		register(_->ins.screen instanceof DialogScreen<?>, BOOLEAN, "isdialogopen");
		register(_->ins.screen instanceof InventoryScreen
		        || ins.screen instanceof CreativeModeInventoryScreen, BOOLEAN, "isinventoryopen");
	}
}
