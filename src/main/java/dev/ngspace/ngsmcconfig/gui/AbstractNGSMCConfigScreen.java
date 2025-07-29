package dev.ngspace.ngsmcconfig.gui;

import java.util.List;

import dev.ngspace.ngsmcconfig.NGSMCConfigCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class AbstractNGSMCConfigScreen extends Screen {
	
	protected Screen parent;
	protected List<NGSMCConfigCategory> categories;
	
	ContainerWidget container;
	Button saveButton;
	Button backButton;
	private boolean createContainer;
	
	protected AbstractNGSMCConfigScreen(Screen parentScreen, List<NGSMCConfigCategory> categories, boolean c) {
		super(Component.literal("NGSMCConfig"));
		this.categories = categories;
		this.parent = parentScreen;
		this.createContainer = c;
	}
	@Override
	protected void init() {
		backButton = Button.builder(Component.literal("Back"), b->onClose())
				.bounds(0, 0, 30, 20)
				.build();
		addRenderableWidget(backButton);
		saveButton = Button.builder(Component.literal("Save"), b->onClose())
				.bounds(30, 0, 30, 20)
				.build();
		addRenderableWidget(saveButton);

		if (createContainer) {
			int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
			int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
			int padding = (int) (width * 0.125);
		
			container = new ContainerWidget(Minecraft.getInstance(), width-padding*2, height, 0);
			container.setPosition(padding, 0);
			
			addRenderableWidget(container);
		}
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parent);
	}
	
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (container!=null)
        	container.render(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
