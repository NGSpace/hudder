package dev.ngspace.ngsmcconfig.api;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public abstract class AbstractNGSMCConfigOptionBuilder<T> {
	
	protected T value;
	protected T defaultValue;
	protected Component name;
	protected Consumer<T> saveOperation;
	protected Function<T, Component> validator;

	protected AbstractNGSMCConfigOptionBuilder(T value, Component name) {
		this.value = value;
		this.defaultValue = value;
		this.name = name;
		this.saveOperation = v->{};
	}
	
	public AbstractNGSMCConfigOptionBuilder<T> setValue(T value) {
		this.value = value;
		return this;
	}

	public AbstractNGSMCConfigOptionBuilder<T> setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public AbstractNGSMCConfigOptionBuilder<T> setName(Component name) {
		this.name = name;
		return this;
	}

	public AbstractNGSMCConfigOptionBuilder<T> setSaveOperation(Consumer<T> saveOperation) {
		this.saveOperation = saveOperation;
		return this;
	}

	public AbstractNGSMCConfigOptionBuilder<T> setHoverComponent(MutableComponent hovercomponent) {
		if (name instanceof MutableComponent mutablename) {
			mutablename.withStyle(s -> s.withHoverEvent(new HoverEvent.ShowText(hovercomponent)));
		}
		return this;
	}

	public AbstractNGSMCConfigOptionBuilder<T> setValidator(Function<T, Component> validator) {
		this.validator = validator;
		return this;
	}

	public abstract AbstractNGSMCConfigOption<T> build();
}
