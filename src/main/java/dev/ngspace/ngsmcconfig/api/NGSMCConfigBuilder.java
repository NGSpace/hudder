package dev.ngspace.ngsmcconfig.api;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import dev.ngspace.ngsmcconfig.gui.NGSMCConfigOptionsScreen;
import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NGSMCConfigBuilder {
	
	Screen parent;
	List<NGSMCConfigCategory> categories = new ArrayList<NGSMCConfigCategory>();
	Runnable writeoperation = () -> {};
	URI docsUri;
	File configfile;
	
	public NGSMCConfigBuilder(Screen parent) {
		this.parent = parent;
	}
	
	public NGSMCConfigCategory createCategory(Component title) {
		return createCategory(title, null);
	}
	
	public NGSMCConfigCategory createCategory(Component title, NGSMCConfigIcon icon) {
		NGSMCConfigCategory category = new NGSMCConfigCategory(title,
				new ArrayList<AbstractNGSMCConfigOption<?>>(),
				icon);
		categories.add(category);
		return category;
	}
	
	public Screen build() {
		return new NGSMCConfigOptionsScreen(parent, categories, categories.get(0), writeoperation, docsUri, configfile, null);
	}

	public void setWriteOperation(Runnable writeoperation) {
		this.writeoperation = writeoperation;
	}

	public void setDocsUri(URI docsUri) {
		this.docsUri = docsUri;
	}

	public void setConfigFile(File configfile) {
		this.configfile = configfile;
	}

	public void removeCategory(NGSMCConfigCategory category) {
		categories.remove(category);
	}
}
