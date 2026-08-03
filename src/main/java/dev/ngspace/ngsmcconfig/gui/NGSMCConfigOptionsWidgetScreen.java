package dev.ngspace.ngsmcconfig.gui;

import java.io.File;
import java.net.URI;
import java.util.List;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NGSMCConfigOptionsWidgetScreen extends AbstractNGSMCConfigScreen {

	private NGSMCConfigCategory selectedCategory;

	public NGSMCConfigOptionsWidgetScreen(Screen parentScreen, List<NGSMCConfigCategory> categories,
			NGSMCConfigCategory selectedCategory, Runnable writeoperation, URI wikiUri, File configfile,
			AbstractNGSMCConfigScreen root, Component configButtonText) {
		super(parentScreen, categories, writeoperation, wikiUri, configfile, root, configButtonText);
		this.selectedCategory = selectedCategory;
	}

	public NGSMCConfigOptionsWidgetScreen(AbstractNGSMCConfigScreen parentScreen,
			NGSMCConfigCategory selectedCategory, AbstractNGSMCConfigScreen root) {
		super(parentScreen, root);
		this.selectedCategory = selectedCategory;
	}
	
	@Override
	protected void init() {
		super.init();
		
		int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		
		if (selectedCategory.customWidget() instanceof AbstractSelectionList<?> list)
			list.updateSizeAndPosition(width-BUTTONS_WIDTH, height-TOP_ROW_SIZE, BUTTONS_WIDTH, TOP_ROW_SIZE);
		else
			selectedCategory.customWidget().setRectangle(width-BUTTONS_WIDTH, height-TOP_ROW_SIZE, BUTTONS_WIDTH, TOP_ROW_SIZE);
		
		addRenderableWidget(selectedCategory.customWidget());
	}

	@Override
	public NGSMCConfigCategory getSelectedCategory() {
		return selectedCategory;
	}
}
