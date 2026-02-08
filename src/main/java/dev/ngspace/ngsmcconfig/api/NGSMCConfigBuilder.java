package dev.ngspace.ngsmcconfig.api;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import dev.ngspace.ngsmcconfig.gui.NGSMCConfigCategorySelectionScreen;
import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NGSMCConfigBuilder {
	
	Screen parent;
	List<NGSMCConfigCategory> categories = new ArrayList<NGSMCConfigCategory>();
	Runnable writeoperation = () -> {};
	URI wikiUri;
	File configfile;
	
	public NGSMCConfigBuilder(Screen parent) {
		this.parent = parent;
	}
	
	public NGSMCConfigCategory createCategory(Component title) {
		NGSMCConfigCategory category = new NGSMCConfigCategory(title, new ArrayList<AbstractNGSMCConfigOption<?>>());
		categories.add(category);
		return category;
	}
	
	public Screen build() {
		return new NGSMCConfigCategorySelectionScreen(parent, categories, writeoperation, wikiUri, configfile);
	}

	public void setWriteOperation(Runnable writeoperation) {
		this.writeoperation = writeoperation;
	}

	public void setWikiUri(URI wikiUri) {
		this.wikiUri = wikiUri;
	}

	public void setConfigFile(File configfile) {
		this.configfile = configfile;
	}

	public void removeCategory(NGSMCConfigCategory category) {
		categories.remove(category);
	}
}
