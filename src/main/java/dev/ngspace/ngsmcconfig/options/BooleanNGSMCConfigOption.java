package dev.ngspace.ngsmcconfig.options;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.api.AbstractNGSMCConfigOptionBuilder;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigIcon;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigButton;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigEntry;
import net.minecraft.network.chat.Component;

public class BooleanNGSMCConfigOption extends AbstractNGSMCConfigOption<Boolean> {
	
	protected BooleanNGSMCConfigOption(Boolean defaultValue, Boolean value, Component text,
			Consumer<Boolean> saveOperation, Function<Boolean, Component> validator) {
		super(defaultValue, value, text, saveOperation, validator);
	}

	Function<Boolean, Component> yesno = b->Component.translatable("ngsmcconfig."+b);
	NGSMCConfigButton widget;

	@Override
	public NGSMCConfigEntry buildEntry() {
        widget = new NGSMCConfigButton(150, 20, 50, 20,
    		Component.literal(""),
    		button->{
				edited = true;
	        	value = !value;
	        	widget.setIcon(new NGSMCConfigIcon.SpriteIcon("gui",
	        			Boolean.TRUE.equals(value) ? "widget/checkbox_selected" : "widget/checkbox"));
	            button.setMessage(yesno.apply(value));
	        },
    		0xFFFFFFFF,
    		new NGSMCConfigIcon.SpriteIcon("gui", "widget/checkbox"));
        widget.setMessage(yesno.apply(value));
        
    	widget.setIcon(new NGSMCConfigIcon.SpriteIcon("gui",
    			Boolean.TRUE.equals(value) ? "widget/checkbox_selected" : "widget/checkbox"));
    	widget.setOutlineColor(0xFFa0a0a0);
    	
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
