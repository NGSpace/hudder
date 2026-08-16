package dev.ngspace.ngsmcconfig.api;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import dev.ngspace.ngsmcconfig.gui.NGSMCConfigEntry;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigOptionsScreen;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigOptionsWidgetScreen;
import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NGSMCConfigBuilder {
	
	Screen parent;
	List<NGSMCConfigCategory> categories = new ArrayList<NGSMCConfigCategory>();
	Runnable writeoperation = () -> {};
	URI docsUri;
	File configfile;
	Component configButtonText = Component.translatable("ngsmcconfig.config");
	Consumer<List<Path>> dragAndDrop;

	public NGSMCConfigBuilder(Screen parent) {
		this.parent = parent;
	}
	
	public NGSMCConfigCategory createCategory(Component title) {
		return createCategory(title, null);
	}
	
	public NGSMCConfigCategory createCategory(Component title, NGSMCConfigIcon icon) {
		NGSMCConfigCategory category = new NGSMCConfigCategory(title,
				new ArrayList<AbstractNGSMCConfigOption<?>>(),
				icon,
				null,
				null);
		categories.add(category);
		return category;
	}

	public void addCustomWidgetCategory(Component title, NGSMCConfigIcon icon, AbstractWidget widget,
			Runnable save, Runnable reset, Supplier<Component> error, Supplier<Component> warning) {
		NGSMCConfigCategory category = new NGSMCConfigCategory(title,
				new ArrayList<AbstractNGSMCConfigOption<?>>(),
				icon,
				null,
				widget);
		category.addOption(new AbstractNGSMCConfigOption<String>(null, null, null, _->save.run(),
				_->error.get(), _->warning.get()) {

			@Override
			public NGSMCConfigEntry buildEntry() {
				return null;
			}

			@Override
			public void reset() {
				reset.run();
			}
		});
		
		
		categories.add(category);
	}
	
	public Screen build() {
		if (categories.isEmpty())
			throw new IllegalStateException("Cannot build screen without any categories");
		
		var category = categories.get(0);
		
		if (category.customWidget() != null) {
			return new NGSMCConfigOptionsWidgetScreen(parent, categories, category, writeoperation, docsUri, configfile,
					null, configButtonText, dragAndDrop);
		}
		if (category.customAction() != null) {
			category.customAction().run();
		}
		return new NGSMCConfigOptionsScreen(parent, categories, category, writeoperation, docsUri, configfile, null,
				configButtonText, dragAndDrop);
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
	
	public Component getConfigButtonText() {
		return configButtonText;
	}

	public void setConfigButtonText(Component configButtonText) {
		this.configButtonText = configButtonText;
	}
	
	public void setDragAndDropConsumer(Consumer<List<Path>> dragAndDrop) {
		this.dragAndDrop = dragAndDrop;
	}

	public void addCustomButton(Component title, NGSMCConfigIcon icon, Runnable runnable) {
		
		if (runnable==null)
			throw new NullPointerException("Provided runnable for NGSMCConfig Custom Button must not be null!");
		
		NGSMCConfigCategory category = new NGSMCConfigCategory(title,
				new ArrayList<AbstractNGSMCConfigOption<?>>(),
				icon,
				runnable,
				null);
		categories.add(category);
	}
}
