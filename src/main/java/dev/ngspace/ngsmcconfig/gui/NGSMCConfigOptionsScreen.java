package dev.ngspace.ngsmcconfig.gui;

import java.util.List;

import dev.ngspace.ngsmcconfig.NGSMCConfigCategory;
import net.minecraft.client.gui.screens.Screen;

public class NGSMCConfigOptionsScreen extends AbstractNGSMCConfigScreen {

	private NGSMCConfigCategory selectedCategory;

	protected NGSMCConfigOptionsScreen(Screen parentScreen, List<NGSMCConfigCategory> categories,
			NGSMCConfigCategory selectedCategory) {
		super(parentScreen, categories, true);
		this.selectedCategory = selectedCategory;
	}
	
	@Override
	protected void init() {
		super.init();
		
		for (var option : selectedCategory.options()) {
			container.addEntry(option.buildEntry());
		}
	}
}
