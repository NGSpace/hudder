package dev.ngspace.ngsmcconfig.gui;

import java.io.File;
import java.net.URI;
import java.util.List;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public abstract class AbstractNGSMCConfigScreen extends Screen {

	public static final int TOP_ROW_SIZE = 22;
	public static final int BOTTOM_ROW_SIZE = 40;
	public static final int BUTTONS_WIDTH = 135;
	
	protected Screen parent;
	protected List<NGSMCConfigCategory> categories;
	protected NGSMCCategoryList categoryContainer;
	
	protected NGSMCConfigButton saveButton;
	protected NGSMCConfigButton backButton;
	protected NGSMCConfigButton globalResetButton;
	protected NGSMCConfigButton wikiButton;
	protected NGSMCConfigButton configButton;
	protected StringWidget errorWidget;
	protected Runnable writeoperation;
	protected URI docsUri;
	protected File configfile;
	protected final AbstractNGSMCConfigScreen root;
	protected final Component configButtonText;

	protected AbstractNGSMCConfigScreen(Screen parent, List<NGSMCConfigCategory> categories,
			Runnable writeoperation, URI docsUri, File configfile, AbstractNGSMCConfigScreen root,
			Component configButtonText) {
		super(Component.literal("NGSMCConfig"));
		this.categories = categories;
		this.parent = parent;
		this.writeoperation = writeoperation;
		this.docsUri = docsUri;
		this.configfile = configfile;
		this.root = root == null ? this : root;
		this.configButtonText = configButtonText;
	}
	
	protected AbstractNGSMCConfigScreen(AbstractNGSMCConfigScreen parent, AbstractNGSMCConfigScreen root) {
		super(Component.literal("NGSMCConfig"));
		this.categories = parent.categories;
		this.parent = parent;
		this.writeoperation = parent.writeoperation;
		this.docsUri = parent.docsUri;
		this.configfile = parent.configfile;
		this.root = root == null ? this : root;
		this.configButtonText = parent.configButtonText;
	}
	@Override
	protected void init() {
		
		Component error = getError();
		
		backButton = new NGSMCConfigButton(0, 0, BUTTONS_WIDTH/2, 20,
				Component.translatable("ngsmcconfig.back"),
				_->onClose(),
				0xFFFFFFFF,
				new NGSMCConfigIcon.SpriteIcon("items", "item/arrow"));
		
		backButton.setOutlineColor(0xFFa0a0a0);
		backButton.setBackgroundColor(0x40000000);
		backButton.setHoveredBackgroundColor(0x30FFFFFF);
		addRenderableWidget(backButton);
		
		saveButton = new NGSMCConfigButton(BUTTONS_WIDTH/2, 0, BUTTONS_WIDTH/2+1, 20,
				Component.translatable("ngsmcconfig.save"),
				_->save(),
				0xFFFFFFFF,
				new NGSMCConfigIcon.SpriteIcon("gui", "pending_invite/accept"));

		saveButton.setOutlineColor(0xFFa0a0a0);
		saveButton.setBackgroundColor(0x40000000);
		saveButton.setHoveredBackgroundColor(0x30FFFFFF);
		saveButton.setDisabledIcon(new NGSMCConfigIcon.SpriteIcon("gui", "pending_invite/reject"));
		
		saveButton.active = error==null;
		
		addRenderableWidget(saveButton);
		
		initCategoryButtons();
		
		globalResetButton = new NGSMCConfigButton(0, height-BOTTOM_ROW_SIZE+1, BUTTONS_WIDTH, 20,
				Component.translatable("ngsmcconfig.globalreset"), _->reset(), 0xFFdb3b3b,
				new NGSMCConfigIcon.SpriteIcon("items", "item/flint_and_steel"));
		
		globalResetButton.setOutlineColor(0xFFdb3b3b);
		globalResetButton.setCenterText(true);
		globalResetButton.setBackgroundColor(0x22db3b3b);
		globalResetButton.setFocusedBackgroundColor(0x22f96363);
		globalResetButton.setHoveredBackgroundColor(0x22f97a7a);
		
		addRenderableWidget(globalResetButton);
		
		int resetBottom = height-BOTTOM_ROW_SIZE+globalResetButton.getHeight() + 1;
		
		if (docsUri!=null) {
			wikiButton = new NGSMCConfigButton(0, resetBottom, BUTTONS_WIDTH/3, 20,
					Component.translatable("ngsmcconfig.wiki"),
					_->clickUrlAction(Minecraft.getInstance(), this, docsUri),
					0xFFFFFFFF,
					new NGSMCConfigIcon.SpriteIcon("items", "item/knowledge_book"));

			wikiButton.setOutlineColor(0xFFa0a0a0);
			wikiButton.setBackgroundColor(0x40000000);
			wikiButton.setHoveredBackgroundColor(0x30FFFFFF);
			addRenderableWidget(wikiButton);
		}

		if (configfile!=null) {
			configButton = new NGSMCConfigButton(docsUri!=null?BUTTONS_WIDTH/3:BUTTONS_WIDTH, resetBottom,
					(BUTTONS_WIDTH/3)*2, 20,
					configButtonText,
					_->Util.getPlatform().openFile(configfile),
					0xFFFFFFFF,
					new NGSMCConfigIcon.SpriteIcon("items", "item/writable_book"));

			configButton.setOutlineColor(0xFFa0a0a0);
			configButton.setBackgroundColor(0x40000000);
			configButton.setHoveredBackgroundColor(0x30FFFFFF);
			addRenderableWidget(configButton);
		}
		
		errorWidget = new StringWidget(stylizeErrorComponment(error), font);
		errorWidget.setPosition(BUTTONS_WIDTH+2, 0);
		errorWidget.setSize(width-BUTTONS_WIDTH-2, 20);
		addRenderableWidget(errorWidget);
	}

	protected void initCategoryButtons() {
		int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
	
		categoryContainer = new NGSMCCategoryList(Minecraft.getInstance(), BUTTONS_WIDTH,
				height-TOP_ROW_SIZE-(configfile != null || wikiButton != null ? BOTTOM_ROW_SIZE : 0), TOP_ROW_SIZE);
		
		for (var category : categories) {
			categoryContainer.addCategory(this, category, category == getSelectedCategory());
		}
		
		addRenderableWidget(categoryContainer);
	}
	
	public abstract NGSMCConfigCategory getSelectedCategory();

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
		minecraft.gui.setScreen(new ConfirmScreen(b->{if (b) resetNoConf();minecraft.gui.setScreen(this);},
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
		if (isEditedAndNotSaved())
			minecraft.gui.setScreen(new ConfirmScreen(b->minecraft.gui.setScreen(b?root.parent:this),
					Component.translatable("ngsmcconfig.confirmunsavedexit"),
					Component.translatable("ngsmcconfig.confirmunsavedexit.text"),
					Component.translatable("ngsmcconfig.confirmunsavedexit.yes"),
					Component.translatable("ngsmcconfig.confirmunsavedexit.no")));
		else
			this.minecraft.gui.setScreen(this.root.parent);
	}
	
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		Component error = getError();
		
		saveButton.active = error==null;
		errorWidget.setMessage(stylizeErrorComponment(error));
		
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

	private Component stylizeErrorComponment(Component error) {
		return error!=null?error.plainCopy().withColor(0xFF0000):Component.literal("");
	}
}
