package dev.ngspace.ngsmcconfig.options;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import dev.ngspace.ngsmcconfig.gui.NGSMCConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public abstract class AbstractTextFieldNGSMCConfigOption<T> extends AbstractNGSMCConfigOption<T> {

	protected Predicate<String> textfilter = e->true;
	int max_length = Integer.MAX_VALUE;
	EditBox widget;

	protected AbstractTextFieldNGSMCConfigOption(T defaultValue, T value, Component text, Consumer<T> saveOperation,
			Function<T, Component> validator) {
		super(defaultValue, value, text, saveOperation, validator);
	}

	@Override
	public NGSMCConfigEntry buildEntry() {
		widget = new EditBox(Minecraft.getInstance().font, 0, 0, 100, 20, Component.literal(""));
		widget.setFilter(e->{
			edited = true;
			return textfilter.test(e);
		});
		widget.setMaxLength(max_length);
		widget.setValue(stringify());
		return new NGSMCConfigEntry(widget, text, this);
	}
	
	protected String stringify() {
		return String.valueOf(value);
	}

	@Override
	public void reset() {
		edited = true;
		value = defaultValue;
		widget.setValue(stringify());
	}
}
