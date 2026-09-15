package dev.ngspace.hudder.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.gui.screens.Screen;

public class HudderModMenuImpl implements ModMenuApi {
	@Override public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
		return HudderNGSMCConfigMenu::createMenu;
    }
}