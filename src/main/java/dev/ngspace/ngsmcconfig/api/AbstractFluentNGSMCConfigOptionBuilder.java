package dev.ngspace.ngsmcconfig.api;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

// Yeah the name was AI generated cause I'm not creative enough and didn't want it to collide with the
// existing AbstractNGSMCConfigOptionBuilder
@SuppressWarnings({ "removal", "deprecation" })
public abstract class AbstractFluentNGSMCConfigOptionBuilder<T,
		E extends AbstractFluentNGSMCConfigOptionBuilder<T, ?>>
		extends AbstractNGSMCConfigOptionBuilder<T> {
	
	protected T value;
	protected T defaultValue;
	protected Component name;
	protected Consumer<T> saveOperation;
	protected Function<T, Component> validator;

	protected AbstractFluentNGSMCConfigOptionBuilder(T value, Component name) {
		super(value, name);
		this.value = value;
		this.defaultValue = value;
		this.name = name;
		this.saveOperation = _->{};
	}

	protected abstract E self();
	
	@Override
	public E setValue(T value) {
		this.value = value;
		return self();
	}

	@Override
	public E setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
		return self();
	}

	@Override
	public E setName(Component name) {
		this.name = name;
		return self();
	}

	@Override
	public E setSaveOperation(Consumer<T> saveOperation) {
		this.saveOperation = saveOperation;
		return self();
	}

	@Override
	public E setHoverComponent(MutableComponent hovercomponent) {
		if (name instanceof MutableComponent mutablename) {
			mutablename.withStyle(s -> s.withHoverEvent(new HoverEvent.ShowText(hovercomponent)));
		}
		return self();
	}

	@Override
	public E setValidator(Function<T, Component> validator) {
		this.validator = validator;
		return self();
	}

	public abstract AbstractNGSMCConfigOption<T> build();
	
}
