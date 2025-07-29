package dev.ngspace.ngsmcconfig;

import java.util.ArrayList;
import java.util.List;

import dev.ngspace.ngsmcconfig.gui.NGSMCConfigCategorySelectionScreen;
import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NGSMCConfigBuilder {
	
	Screen parent;
	List<NGSMCConfigCategory> categories = new ArrayList<NGSMCConfigCategory>();
	
	public NGSMCConfigBuilder(Screen parent) {
		this.parent = parent;
	}
	
	public NGSMCConfigCategory createCategory(Component title) {
		NGSMCConfigCategory category = new NGSMCConfigCategory(title, new ArrayList<AbstractNGSMCConfigOption<?>>());
		categories.add(category);
		return category;
	}
	
	public Screen build() {
		return new NGSMCConfigCategorySelectionScreen(parent, categories);
	}
}
