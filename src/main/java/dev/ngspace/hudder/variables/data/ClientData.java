package dev.ngspace.hudder.variables.data;

import dev.ngspace.hudder.variables.HudderBuiltInVariables;
import dev.ngspace.hudder.variables.advanced.Misc;
import net.fabricmc.loader.api.FabricLoader;
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
		registerBoolean(_->ins.gameRenderer.mainCamera().entity() != ins.player, "camera_detached");
		
		// Resource packs
		registerObject(_->ins.getResourcePackRepository().getSelectedPacks().stream()
				.filter(pack->!pack.isRequired())
				.map(t -> t.getTitle().getString())
				.toList(), "selectedresourcepacks");
		registerObject(_->ins.getResourcePackRepository().getSelectedPacks().stream()
				.map(t -> t.getTitle().getString())
				.toList(), "selectedresourcepacks_unfiltered");
		registerObject(_->FabricLoader.getInstance().getAllMods().stream()
				.map(mod-> mod.getMetadata().getId() + ":" + mod.getMetadata().getVersion())
				.toList(), "mods_list");
	}

	private static void registerInputVariables() {
		// Mouse buttons
		registerBoolean(_->ins.mouseHandler.isLeftPressed(), "mouse_left");
		registerBoolean(_->ins.mouseHandler.isMiddlePressed(), "mouse_middle");
		registerBoolean(_->ins.mouseHandler.isRightPressed(), "mouse_right");

		// Clicks per second
		registerNumber(_->Misc.getLeftCPS() + Misc.getRightCPS(), "cps");
		registerNumber(_->Misc.getLeftCPS(), "cps_left");
		registerNumber(_->Misc.getRightCPS(), "cps_right");
	}

	private static void registerScreenVariables() {
		// Window / GUI
		registerNumber(_->ins.getWindow().getGuiScaledWidth(), "width");
		registerNumber(_->ins.getWindow().getGuiScaledHeight(), "height");
		registerNumber(_->ins.getWindow().getGuiScale(), "guiscale");

		// Open GUI
		registerString(_->Misc.getScreenType(ins.gui.screen()), "openguitype");

		registerString(_->(ins.gui.screen() == null) ? null : ins.gui.screen().getTitle().getString(), "openguititle");

		// HUD / debug
		registerBoolean(_->ins.gui.hud.isHidden(), "hudhidden");
		registerBoolean(_->ins.getDebugOverlay().showDebugScreen(), "showdebug");
		registerBoolean(_->ins.debugEntries.isOverlayVisible(), "f3enabled");
	}
	
	private static void registerScreenTypeVariables() {
		registerBoolean(_->ins.gui.screen() != null, "isguiopen");
		registerBoolean(_->ins.gui.screen() instanceof ContainerScreen, "ischestopen");
		registerBoolean(_->ins.gui.screen() instanceof CraftingScreen, "iscraftingtableopen");
		registerBoolean(_->ins.gui.screen() instanceof ChatScreen, "ischatopen");
		registerBoolean(_->ins.gui.screen() instanceof DialogScreen<?>, "isdialogopen");
		registerBoolean(_->ins.gui.screen() instanceof InventoryScreen
		        || ins.gui.screen() instanceof CreativeModeInventoryScreen, "isinventoryopen");
		registerBoolean(_->ins.options.keyPlayerList.isDown()
				&& (!ins.isLocalServer() || ins.player.connection.getListedOnlinePlayers().size() > 1),
				"is_player_list_shown");
	}
}
