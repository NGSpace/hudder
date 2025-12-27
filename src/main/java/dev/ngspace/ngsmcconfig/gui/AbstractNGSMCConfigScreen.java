package dev.ngspace.ngsmcconfig.gui;

import java.io.File;
import java.net.URI;
import java.util.List;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public abstract class AbstractNGSMCConfigScreen extends Screen {
	
	protected Screen parent;
	protected List<NGSMCConfigCategory> categories;
	protected NGSMCConfigOptionsListWidget container;
	protected boolean createContainer;
	protected boolean askBeforeUnsavedLeave;
	
	protected Button saveButton;
	protected Button backButton;
	protected Button globalResetButton;
	protected Button wikiButton;
	protected Button configButton;
	protected StringWidget errorWidget;
	protected Runnable writeoperation;
	protected URI wikiUri;
	protected File configfile;
	
	protected AbstractNGSMCConfigScreen(Screen parentScreen, List<NGSMCConfigCategory> categories, boolean c,
			Runnable writeoperation, URI wikiUri, File configfile) {
		super(Component.literal("NGSMCConfig"));
		this.categories = categories;
		this.parent = parentScreen;
		this.createContainer = c;
		this.writeoperation = writeoperation;
		this.wikiUri = wikiUri;
		this.configfile = configfile;
	}
	@Override
	protected void init() {
		
		Component error = getError();
		
		backButton = Button.builder(Component.translatable("ngsmcconfig.back"), b->onClose())
				.bounds(0, 0, 30, 20)
				.build();
		addRenderableWidget(backButton);
		
		saveButton = Button.builder(Component.translatable("ngsmcconfig.save"), b->save())
				.bounds(30, 0, 30, 20)
				.build();
		saveButton.active = error==null;
		addRenderableWidget(saveButton);
		
		globalResetButton = Button.builder(Component.translatable("ngsmcconfig.globalreset").withColor(0xdb3b3b),
				b->reset())
				.bounds(width-40, 0, 40, 20)
				.build();

		if (configfile!=null) {
			configButton = Button.builder(Component.translatable("ngsmcconfig.config"),
					b->Util.getPlatform().openFile(configfile))
					.bounds(width-(wikiUri!=null?150:120), 0, 70, 20)
					.build();
			addRenderableWidget(configButton);
		}
		
		if (wikiUri!=null) {
			wikiButton = Button.builder(Component.translatable("ngsmcconfig.wiki"),
					b->clickUrlAction(Minecraft.getInstance(), this, wikiUri))
					.bounds(width-80, 0, 40, 20)
					.build();
			addRenderableWidget(wikiButton);
		}
		addRenderableWidget(globalResetButton);
		
		errorWidget = new StringWidget(stylizeErrorComponment(error), font);
		errorWidget.setPosition(65, 0);
		errorWidget.setSize(300, 20);
//		errorWidget.alignLeft();
		addRenderableWidget(errorWidget);
		
		if (createContainer) {
			int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
			int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		
			container = new NGSMCConfigOptionsListWidget(Minecraft.getInstance(), width, height, 35);
			
			addRenderableWidget(container);
		}
	}
	
	protected void save() {
		for (var category : categories) {
			for (var option : category.options()) {
				option.save();
				option.edited = false;
			}
		}
		writeoperation.run();
	}
	protected void reset() {
		minecraft.setScreen(new ConfirmScreen(b->{if (b) resetNoConf();minecraft.setScreen(this);},
				Component.translatable("ngsmcconfig.confirmreset"),
				Component.translatable("ngsmcconfig.confirmreset.text")));
	}
	protected void resetNoConf() {
		for (var category : categories) {
			for (var option : category.options()) {
				option.reset();
			}
		}
		save();
	}
	protected Component getError() {
		for (var category : categories) {
			for (var option : category.options()) {
				var error = option.getError();
				if (error!=null)
					return error;
			}
		}
		return null;
	}
	protected boolean isEditedAndNotSaved() {
		for (var category : categories)
			for (var option : category.options())
				if (option.edited)
					return option.edited;
		return false;
	}
	
	@Override
	public void onClose() {
		if (askBeforeUnsavedLeave&&isEditedAndNotSaved())
			minecraft.setScreen(new ConfirmScreen(b->minecraft.setScreen(b?parent:this),
					Component.translatable("ngsmcconfig.confirmunsavedexit"),
					Component.translatable("ngsmcconfig.confirmunsavedexit.text")));
		else
			this.minecraft.setScreen(this.parent);
	}
	
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		
		Component error = getError();
		
		saveButton.active = error==null;
		errorWidget.setMessage(stylizeErrorComponment(error));
		
        if (container!=null)
        	container.render(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

	private Component stylizeErrorComponment(Component error) {
		return error!=null?error.plainCopy().withColor(0xFF0000):Component.literal("");
	}
}
