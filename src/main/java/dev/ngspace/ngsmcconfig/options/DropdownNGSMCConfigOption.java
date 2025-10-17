package dev.ngspace.ngsmcconfig.options;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.api.AbstractNGSMCConfigOptionBuilder;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigEntry;
import dev.ngspace.ngsmcconfig.gui.dropdown.DropdownElement;
import net.minecraft.network.chat.Component;

/**
 * Failed Experiment, does not work, do not use.
 */
public class DropdownNGSMCConfigOption extends AbstractNGSMCConfigOption<Boolean> {
	
	protected DropdownNGSMCConfigOption(Boolean defaultValue, Boolean value, Component text,
			Consumer<Boolean> saveOperation, Function<Boolean, Component> validator) {
		super(defaultValue, value, text, saveOperation, validator);
	}

	Function<Boolean, Component> yesno = b->Component.translatable("ngsmcconfig."+b);
	DropdownElement widget;

	@Override
	public NGSMCConfigEntry buildEntry() {
        widget = new DropdownElement(10,10,100, 20, Component.literal("test"));
        widget.setMessage(yesno.apply(value));
		return new NGSMCConfigEntry(widget, text, this, true);
	}
	
	public static AbstractNGSMCConfigOptionBuilder<Boolean> builder(boolean value, Component name) {
		return new AbstractNGSMCConfigOptionBuilder<Boolean>(value, name) {
			@Override public AbstractNGSMCConfigOption<Boolean> build() {
				return new DropdownNGSMCConfigOption(defaultValue, value, name, saveOperation, validator);
			}
		};
	}

	@Override
	public void reset() {
		edited = true;
    	value = defaultValue;
    	if (widget!=null)
    		widget.setMessage(yesno.apply(value));
	}
}
