package dev.ngspace.ngsmcconfig.options;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.api.AbstractNGSMCConfigOptionBuilder;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigEntry;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class BooleanNGSMCConfigOption extends AbstractNGSMCConfigOption<Boolean> {
	
	protected BooleanNGSMCConfigOption(Boolean defaultValue, Boolean value, Component text,
			Consumer<Boolean> saveOperation, Function<Boolean, Component> validator) {
		super(defaultValue, value, text, saveOperation, validator);
	}

	Function<Boolean, Component> yesno = b->Component.translatable("ngsmcconfig."+b);
	Button widget;

	@Override
	public NGSMCConfigEntry buildEntry() {
        widget = Button.builder(Component.literal(""), button->{
			edited = true;
        	value = !value;
            button.setMessage(yesno.apply(value));
        }).size(100, 20).build();
        widget.setMessage(yesno.apply(value));
		return new NGSMCConfigEntry(widget, text, this);
	}
	
	public static AbstractNGSMCConfigOptionBuilder<Boolean> builder(boolean value, Component name) {
		return new AbstractNGSMCConfigOptionBuilder<Boolean>(value, name) {
			@Override public AbstractNGSMCConfigOption<Boolean> build() {
				return new BooleanNGSMCConfigOption(defaultValue, value, name, saveOperation, validator);
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
