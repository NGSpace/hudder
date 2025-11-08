package dev.ngspace.ngsmcconfig.gui;

import java.io.File;
import java.net.URI;
import java.util.List;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import net.minecraft.client.gui.screens.Screen;

public class NGSMCConfigOptionsScreen extends AbstractNGSMCConfigScreen {

	private NGSMCConfigCategory selectedCategory;

	protected NGSMCConfigOptionsScreen(Screen parentScreen, List<NGSMCConfigCategory> categories,
			NGSMCConfigCategory selectedCategory, Runnable writeoperation, URI wikiUri, File configfile) {
		super(parentScreen, categories, true, writeoperation, wikiUri, configfile);
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
