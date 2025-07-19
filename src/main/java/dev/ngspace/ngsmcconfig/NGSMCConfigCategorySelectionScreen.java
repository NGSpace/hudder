package dev.ngspace.ngsmcconfig;

import java.util.List;

import net.minecraft.client.gui.screens.Screen;

public class NGSMCConfigCategorySelectionScreen extends AbstractNGSMCConfigScreen {

	public NGSMCConfigCategorySelectionScreen(Screen parentScreen,
			List<NGSMCConfigCategory> categories) {
		super(parentScreen, categories);
		container.addEntry(new NGSMCConfigEntry());
	}
	
}