package dev.ngspace.ngsmcconfig;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NGSMCConfigBuilder {
	
	Screen parent;
	List<NGSMCConfigCategory> categories;
	
	public NGSMCConfigBuilder(Screen parent) {
		this.parent = parent;
	}
	
	public NGSMCConfigCategory createCategory(String name, Component title) {
		NGSMCConfigCategory category = new NGSMCConfigCategory(name, title,
				new ArrayList<AbstractNGSMCConfigOption<?>>());
		categories.add(category);
		return category;
	}
	
	public Screen build() {
		return new NGSMCConfigCategorySelectionScreen(parent, categories);
	}
}
