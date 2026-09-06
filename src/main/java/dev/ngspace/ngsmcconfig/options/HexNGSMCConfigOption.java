package dev.ngspace.ngsmcconfig.options;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigOptionBuilder;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigColorPickerWidget;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class HexNGSMCConfigOption extends AbstractNGSMCConfigOption<Integer> {

	protected boolean validnum = true;
	protected String invalidnum;
	protected NGSMCConfigColorPickerWidget widget;
	
	protected HexNGSMCConfigOption(Integer defaultValue, Integer value, Component text, Consumer<Integer> saveOperation,
			Function<Integer, Component> validator, Function<Integer, Component> warning) {
		super(defaultValue, value, text, saveOperation, validator, warning);
		
		this.validator = val->{
			if (!validnum)
				return Component.literal("Invalid hex value: \"" + invalidnum + "\"");
			return validator==null?null:validator.apply(val);
		};
	}

	@Override
	public NGSMCConfigEntry buildEntry() {
		widget = new NGSMCConfigColorPickerWidget(Minecraft.getInstance().font, 0, 0, 100, 20,
				Component.literal(""), value);
		widget.setMaxLength(10);
		widget.setColorAndText(value);
		widget.setResponder(val->{
			edited = true;
			try {
				this.value = Integer.parseUnsignedInt(val.substring(val.charAt(0)=='#' ? 1 : 2), 16);
				if (!widget.isApplyingPickerColor())
					widget.setColor(this.value);
				validnum = true;
			} catch (NumberFormatException | StringIndexOutOfBoundsException _) {
				validnum = false;
				invalidnum = val;
			}
		});
		return new NGSMCConfigEntry(widget, text, this, true);
	}
	
	public static NGSMCConfigOptionBuilder<Integer> builder(int value, Component name) {
		return new NGSMCConfigOptionBuilder<Integer>(value, name) {
			@Override public AbstractNGSMCConfigOption<Integer> build() {
				return new HexNGSMCConfigOption(defaultValue, value, name, saveOperation, validator, warning);
			}
		};
	}

	@Override
	public void reset() {
		edited = true;
		value = defaultValue;
		if (widget!=null)
			widget.setColorAndText(value);
	}
}
