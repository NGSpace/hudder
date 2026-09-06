package dev.ngspace.ngsmcconfig.api;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public abstract class AbstractNGSMCConfigOptionBuilder<T,
		E extends AbstractNGSMCConfigOptionBuilder<T, ?>>  {
	
	protected T value;
	protected T defaultValue;
	protected Component name;
	protected Consumer<T> saveOperation;
	protected Function<T, Component> validator;
	protected Function<T, Component> warning;

	protected AbstractNGSMCConfigOptionBuilder(T value, Component name) {
		this.value = value;
		this.defaultValue = value;
		this.name = name;
		this.saveOperation = _->{};
	}

	protected abstract E self();
	
	public E setValue(T value) {
		this.value = value;
		return self();
	}

	public E setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
		return self();
	}

	public E setName(Component name) {
		this.name = name;
		return self();
	}

	public E setSaveOperation(Consumer<T> saveOperation) {
		this.saveOperation = saveOperation;
		return self();
	}

	public E setHoverComponent(MutableComponent hovercomponent) {
		if (name instanceof MutableComponent mutablename) {
			mutablename.withStyle(s -> s.withHoverEvent(new HoverEvent.ShowText(hovercomponent)));
		}
		return self();
	}

	public E setValidator(Function<T, Component> validator) {
		this.validator = validator;
		return self();
	}

	public E setWarningProvider(Function<T, Component> warning) {
		this.warning = warning;
		return self();
	}

	public abstract AbstractNGSMCConfigOption<T> build();
	
}
