package dev.ngspace.hudder.data_management.builtin;

import static dev.ngspace.hudder.data_management.api.VariableTypes.BOOLEAN;
import static dev.ngspace.hudder.data_management.api.VariableTypes.NUMBER;
import static dev.ngspace.hudder.data_management.api.VariableTypes.STRING;

import dev.ngspace.hudder.data_management.Advanced;
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
	}

	private static void registerInputVariables() {
		// Mouse buttons
		register(k->ins.mouseHandler.isLeftPressed(), BOOLEAN, "mouse_left");
		register(k->ins.mouseHandler.isMiddlePressed(), BOOLEAN, "mouse_middle");
		register(k->ins.mouseHandler.isRightPressed(), BOOLEAN, "mouse_right");

		// Clicks per second
		register(k->Advanced.getLeftCPS() + Advanced.getRightCPS(), NUMBER, "cps");
		register(k->Advanced.getLeftCPS(), NUMBER, "cps_left");
		register(k->Advanced.getRightCPS(), NUMBER, "cps_right");
	}

	private static void registerScreenVariables() {
		// Window / GUI
		register(k->ins.getWindow().getGuiScaledWidth(), NUMBER, "width");
		register(k->ins.getWindow().getGuiScaledHeight(), NUMBER, "height");
		register(k->ins.getWindow().getGuiScale(), NUMBER, "guiscale");

		// Open GUI
		register(k->Advanced.getScreenType(ins.screen), STRING, "openguitype");

		register(k->(ins.screen == null) ? null : ins.screen.getTitle().getString(), STRING, "openguititle");

		// HUD / debug
		register(k->ins.options.hideGui, BOOLEAN, "hudhidden");
		register(k->ins.getDebugOverlay().showDebugScreen(), BOOLEAN, "showdebug");
		register(k->ins.debugEntries.isOverlayVisible(), BOOLEAN, "f3enabled");

		// Camera state
		register(k->ins.gameRenderer.getMainCamera().entity() != ins.player, BOOLEAN, "camera_detached");
	}
	
	private static void registerScreenTypeVariables() {
		register(k->ins.screen != null, BOOLEAN, "isguiopen");
		register(k->ins.screen instanceof ContainerScreen, BOOLEAN, "ischestopen");
		register(k->ins.screen instanceof CraftingScreen, BOOLEAN, "iscraftingtableopen");
		register(k->ins.screen instanceof ChatScreen, BOOLEAN, "ischatopen");
		register(k->ins.screen instanceof DialogScreen<?>, BOOLEAN, "isdialogopen");
		register(k->ins.screen instanceof InventoryScreen
		        || ins.screen instanceof CreativeModeInventoryScreen, BOOLEAN, "isinventoryopen");
	}
}
