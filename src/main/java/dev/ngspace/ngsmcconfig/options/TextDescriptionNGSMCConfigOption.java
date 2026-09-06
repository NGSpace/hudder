package dev.ngspace.ngsmcconfig.options;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigOptionBuilder;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class TextDescriptionNGSMCConfigOption extends AbstractNGSMCConfigOption<String> {
	
	protected TextDescriptionNGSMCConfigOption(Component text) {
		super("", "", text, _->{}, _->null,_->null);
		
	}

	public static NGSMCConfigOptionBuilder<String> builder(Component name) {
		return new NGSMCConfigOptionBuilder<String>("", name) {
			@Override public AbstractNGSMCConfigOption<String> build() {
				return new TextDescriptionNGSMCConfigOption(name);
			}
		};
	}

	@Override
	public NGSMCConfigEntry buildEntry() {
		return new NGSMCConfigEntry(null, Component.empty(), this) {
			@Override
			public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered,
					float partialTick) {
				super.extractContent(graphics, mouseX, mouseY, hovered, partialTick);
				graphics.centeredText(Minecraft.getInstance().font, text, getX()+getWidth()/2, getY(), 0xFFFFFFFF);
			}
		};
	}

	@Override public void reset() {/* Nothin to reset*/}
}

