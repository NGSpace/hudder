package dev.ngspace.ngsmcconfig.options;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.api.AbstractNGSMCConfigOptionBuilder;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class HexNGSMCConfigOption extends AbstractNGSMCConfigOption<Integer> {

	protected boolean validnum = true;
	protected String invalidnum;
	protected EditBox widget;
	
	protected HexNGSMCConfigOption(Integer defaultValue, Integer value, Component text, Consumer<Integer> saveOperation,
			Function<Integer, Component> validator) {
		super(defaultValue, value, text, saveOperation, validator);
		
		this.validator = val->{
			if (!validnum)
				return Component.literal("Invalid hex value: \"" + invalidnum + "\"");
			return validator==null?null:validator.apply(val);
		};
	}

	@Override
	public NGSMCConfigEntry buildEntry() {
		widget = new EditBox(Minecraft.getInstance().font, 0, 0, 100, 20, Component.literal("")) {
			
		};
		widget.setMaxLength(10);
		widget.setValue("#"+String.format("%1$08X",value));
		widget.setFilter(val->{
			edited = true;
			try {
				this.value = Integer.parseUnsignedInt(val.substring(val.charAt(0)=='#' ? 1 : 2), 16);
				validnum = true;
			} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
				validnum = false;
				invalidnum = val;
			}
			return true;
		});
		return new NGSMCConfigEntry(widget, text, this);
	}
	
	public static AbstractNGSMCConfigOptionBuilder<Integer> builder(int value, Component name) {
		return new AbstractNGSMCConfigOptionBuilder<Integer>(value, name) {
			@Override public AbstractNGSMCConfigOption<Integer> build() {
				return new HexNGSMCConfigOption(defaultValue, value, name, saveOperation, validator);
			}
		};
	}

	@Override
	public void reset() {
		edited = true;
		value = defaultValue;
    	if (widget!=null)
    		widget.setValue("#"+String.format("%1$08X",value));
	}
}
