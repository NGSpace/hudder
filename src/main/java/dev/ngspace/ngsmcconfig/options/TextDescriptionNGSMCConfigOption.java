package dev.ngspace.ngsmcconfig.options;

import dev.ngspace.ngsmcconfig.api.AbstractNGSMCConfigOptionBuilder;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TextDescriptionNGSMCConfigOption extends AbstractNGSMCConfigOption<String> {
	
	protected TextDescriptionNGSMCConfigOption(Component text) {
		super("", "", text, t->{}, t->null);
		
	}

	public static AbstractNGSMCConfigOptionBuilder<String> builder(Component name) {
		return new AbstractNGSMCConfigOptionBuilder<String>("", name) {
			@Override public AbstractNGSMCConfigOption<String> build() {
				return new TextDescriptionNGSMCConfigOption(name);
			}
		};
	}

	@Override
	public NGSMCConfigEntry buildEntry() {
		return new NGSMCConfigEntry(null, Component.empty(), this) {
			@Override
			public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered,
					float partialTick) {
				super.renderContent(graphics, mouseX, mouseY, hovered, partialTick);
				graphics.drawCenteredString(Minecraft.getInstance().font, text, getX()+getWidth()/2, getY(), 0xFFFFFFFF);
			}
		};
	}

	@Override public void reset() {/* Nothin to reset*/}
}

