package dev.ngspace.ngsmcconfig.options;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.api.AbstractFluentNGSMCConfigOptionBuilder;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigIcon;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigButton;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class BooleanNGSMCConfigOption extends AbstractNGSMCConfigOption<Boolean> {
	
	private Function<Boolean, Component> componentProvider;
	NGSMCConfigButton widget;

	protected BooleanNGSMCConfigOption(Boolean defaultValue, Boolean value, Component text,
			Consumer<Boolean> saveOperation, Function<Boolean, Component> validator,
			Function<Boolean, Component> componentProvider) {
		super(defaultValue, value, text, saveOperation, validator);
		this.componentProvider = componentProvider;
	}

	@Override
	public NGSMCConfigEntry buildEntry() {
        widget = new NGSMCConfigButton(0, 0,
        		Math.clamp(Minecraft.getInstance().font.width(componentProvider.apply(value))+24l, 20, 100), 20,
    		Component.literal(""),
    		_->{
				edited = true;
	        	value = !value;
	        	updateWidget();
	        },
    		0xFFFFFFFF,
    		new NGSMCConfigIcon.SpriteIcon("gui", "widget/checkbox"));
        widget.setMessage(componentProvider.apply(value));
        
    	widget.setIcon(new NGSMCConfigIcon.SpriteIcon("gui",
    			Boolean.TRUE.equals(value) ? "widget/checkbox_selected" : "widget/checkbox"));
    	widget.setOutlineColor(0xFFa0a0a0);
    	
		return new NGSMCConfigEntry(widget, text, this);
		
	}
	
	private void updateWidget() {
    	widget.setIcon(new NGSMCConfigIcon.SpriteIcon("gui",
    			Boolean.TRUE.equals(value) ? "widget/checkbox_selected" : "widget/checkbox"));
    	widget.setMessage(componentProvider.apply(value));
        widget.setWidth(Math.clamp(Minecraft.getInstance().font.width(widget.getMessage())+24l, 20, 100));
	}

	public static BooleanNGSMCConfigOptionBuilder builder(boolean value, Component name) {
		return new BooleanNGSMCConfigOptionBuilder(value, name) {
		};
	}
	
	public static class BooleanNGSMCConfigOptionBuilder extends AbstractFluentNGSMCConfigOptionBuilder<Boolean,
			BooleanNGSMCConfigOptionBuilder> {
		public BooleanNGSMCConfigOptionBuilder(Boolean value, Component name) {
			super(value, name);
	    }

		private Function<Boolean, Component> componentProvider = b->Component.translatable("ngsmcconfig."+b);

		public BooleanNGSMCConfigOptionBuilder setComponentProvider(Function<Boolean, Component>
				componentProvider) {
			this.componentProvider = componentProvider;
			return this;
		}

		@Override public AbstractNGSMCConfigOption<Boolean> build() {
			return new BooleanNGSMCConfigOption(defaultValue, value, name, saveOperation, validator, componentProvider);
		}

		@Override
		protected BooleanNGSMCConfigOptionBuilder self() {
			return this;
		}
	}

	@Override
	public void reset() {
		edited = true;
    	value = defaultValue;
    	if (widget!=null) updateWidget();
	}
}
