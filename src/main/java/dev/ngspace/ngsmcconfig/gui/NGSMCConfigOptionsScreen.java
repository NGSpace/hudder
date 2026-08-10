package dev.ngspace.ngsmcconfig.gui;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NGSMCConfigOptionsScreen extends AbstractNGSMCConfigScreen {

	private NGSMCConfigCategory selectedCategory;
	protected NGSMCConfigOptionsListWidget container;

	public NGSMCConfigOptionsScreen(Screen parentScreen, List<NGSMCConfigCategory> categories,
			NGSMCConfigCategory selectedCategory, Runnable writeoperation, URI wikiUri, File configfile,
			AbstractNGSMCConfigScreen root, Component configButtonText, Consumer<List<Path>> dragAndDrop) {
		super(parentScreen, categories, writeoperation, wikiUri, configfile, root, configButtonText, dragAndDrop);
		this.selectedCategory = selectedCategory;
	}

	public NGSMCConfigOptionsScreen(AbstractNGSMCConfigScreen parentScreen,
			NGSMCConfigCategory selectedCategory, AbstractNGSMCConfigScreen root) {
		super(parentScreen, root);
		this.selectedCategory = selectedCategory;
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        container.extractOverlayRenderState(graphics, mouseX, mouseY, partialTick);
	}
	
	@Override
	protected void init() {
		super.init();
		
		int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
	
		container = new NGSMCConfigOptionsListWidget(Minecraft.getInstance(), width, height-TOP_ROW_SIZE, BUTTONS_WIDTH, TOP_ROW_SIZE);
		
		addRenderableWidget(container);
		
		for (var option : selectedCategory.options()) {
			container.addEntry(option.buildEntry());
		}
	}

	@Override
	public NGSMCConfigCategory getSelectedCategory() {
		return selectedCategory;
	}
}
