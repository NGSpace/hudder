package dev.ngspace.ngsmcconfig.api;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

/**
 * @deprecated Use {@link AbstractFluentNGSMCConfigOptionBuilder}
 */
@Deprecated(since = "10.1.0", forRemoval = true)
@SuppressWarnings("removal")
public abstract class AbstractNGSMCConfigOptionBuilder<T> {
	
	@Deprecated(since = "10.1.0", forRemoval = true)
	protected T value;
	@Deprecated(since = "10.1.0", forRemoval = true)
	protected T defaultValue;
	@Deprecated(since = "10.1.0", forRemoval = true)
	protected Component name;
	@Deprecated(since = "10.1.0", forRemoval = true)
	protected Consumer<T> saveOperation;
	@Deprecated(since = "10.1.0", forRemoval = true)
	protected Function<T, Component> validator;

	@Deprecated(since = "10.1.0", forRemoval = true)
	protected AbstractNGSMCConfigOptionBuilder(T value, Component name) {
		this.value = value;
		this.defaultValue = value;
		this.name = name;
		this.saveOperation = _->{};
	}
	
	@Deprecated(since = "10.1.0", forRemoval = true)
	public AbstractNGSMCConfigOptionBuilder<T> setValue(T value) {
		this.value = value;
		return this;
	}

	@Deprecated(since = "10.1.0", forRemoval = true)
	public AbstractNGSMCConfigOptionBuilder<T> setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	@Deprecated(since = "10.1.0", forRemoval = true)
	public AbstractNGSMCConfigOptionBuilder<T> setName(Component name) {
		this.name = name;
		return this;
	}

	@Deprecated(since = "10.1.0", forRemoval = true)
	public AbstractNGSMCConfigOptionBuilder<T> setSaveOperation(Consumer<T> saveOperation) {
		this.saveOperation = saveOperation;
		return this;
	}

	@Deprecated(since = "10.1.0", forRemoval = true)
	public AbstractNGSMCConfigOptionBuilder<T> setHoverComponent(MutableComponent hovercomponent) {
		if (name instanceof MutableComponent mutablename) {
			mutablename.withStyle(s -> s.withHoverEvent(new HoverEvent.ShowText(hovercomponent)));
		}
		return this;
	}

	@Deprecated(since = "10.1.0", forRemoval = true)
	public AbstractNGSMCConfigOptionBuilder<T> setValidator(Function<T, Component> validator) {
		this.validator = validator;
		return this;
	}

	@Deprecated(since = "10.1.0", forRemoval = true)
	public abstract AbstractNGSMCConfigOption<T> build();
}
