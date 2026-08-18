package dev.ngspace.ngsmcconfig.options;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.gui.NGSMCConfigEntry;
import net.minecraft.network.chat.Component;

public abstract class AbstractNGSMCConfigOption<T> {

	public T defaultValue;
	public T value;
	public Component text;
	public Consumer<T> saveOperation;
	public Function<T, Component> validator;
	public Function<T, Component> warning;
	public boolean edited;

	protected AbstractNGSMCConfigOption(T defaultValue, T value, Component text, Consumer<T> saveOperation,
			Function<T, Component> validator, Function<T, Component> warning) {
		this.defaultValue = defaultValue;
		this.value = value;
		this.text = text;
		this.saveOperation = saveOperation;
		this.validator = validator;
		this.warning = warning;
	}
	
	public void save() {
		saveOperation.accept(value);
	}
	
	public Component getError() {
		return validator==null ? null : validator.apply(value);
	}
	
	public Component getWarning() {
		return warning==null ? null : warning.apply(value);
	}
	
	public abstract NGSMCConfigEntry buildEntry();

	public abstract void reset();

	public boolean isDefault() {
		return Objects.equals(value, defaultValue);
	}
}
