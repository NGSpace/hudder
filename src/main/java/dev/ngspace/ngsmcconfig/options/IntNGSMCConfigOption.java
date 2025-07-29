package dev.ngspace.ngsmcconfig.options;

import dev.ngspace.ngsmcconfig.NGSMCConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class IntNGSMCConfigOption extends AbstractNGSMCConfigOption<Integer> {

	private IntNGSMCConfigOption(int defaultValue, int value, Component text) {
		super(defaultValue, value, text);
	}

	@Override
	public NGSMCConfigEntry buildEntry() {
		
		var widget = new EditBox(Minecraft.getInstance().font, 0, 0, 30, 20, Component.literal(""));
		return new NGSMCConfigEntry(widget, text);
	}
	
	public static Builder<Integer> builder(int value, Component name) {
		return new Builder<Integer>(value, name) {
			@Override public AbstractNGSMCConfigOption<Integer> build() {
				return new IntNGSMCConfigOption(defaultValue, value, name);
			}
		};
	}
}
