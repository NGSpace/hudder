package dev.ngspace.ngsmcconfig;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class AbstractNGSMCConfigScreen extends Screen {
	
	protected Screen parent;
	protected List<NGSMCConfigCategory> categories;
	ContainerWidget container;
	
	protected AbstractNGSMCConfigScreen(Screen parentScreen, List<NGSMCConfigCategory> categories) {
		super(Component.literal("NGSMCConfig"));
		this.categories = categories;
		this.parent = parentScreen;
		addRenderableWidget(Button.builder(Component.literal("Back"), b->onClose())
				.bounds(0, 0, 30, 20)
				.build());
		addRenderableWidget(Button.builder(Component.literal("Save"), b->onClose())
				.bounds(30, 0, 30, 20)
				.build());
		
		int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		int padding = (int) (width * 0.125);
		
		container = new ContainerWidget(Minecraft.getInstance(), width-padding*2, height, 0, 40);
		container.setPosition(padding, 0);
		
		addRenderableWidget(container);
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parent);
	}
}
