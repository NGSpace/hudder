package dev.ngspace.ngsmcconfig.options;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.api.AbstractFluentNGSMCConfigOptionBuilder;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigDropdownWidget;
import dev.ngspace.ngsmcconfig.gui.NGSMCConfigEntry;
import net.minecraft.network.chat.Component;

public class DropdownNGSMCConfigOption<T> extends AbstractNGSMCConfigOption<T> {

	protected final List<T> options;
	protected final Function<T, Component> valueText;
	protected NGSMCConfigDropdownWidget<T> widget;

	protected DropdownNGSMCConfigOption(T defaultValue, T value, Component text, Consumer<T> saveOperation,
			Function<T, Component> validator, List<T> options, Function<T, Component> valueText) {
		super(defaultValue, value, text, saveOperation, validator);
		this.options = List.copyOf(options);
		this.valueText = valueText;
	}

	@Override
	public NGSMCConfigEntry buildEntry() {
		widget = new NGSMCConfigDropdownWidget<T>(0, 0, 100, 20, options, value, valueText, selectedValue -> {
			value = selectedValue;
			edited = true;
		});
		return new NGSMCConfigEntry(widget, text, this, true);
	}

	@Override
	public void reset() {
		edited = true;
		value = defaultValue;
		if (widget != null)
			widget.setValue(value);
	}

	public static <T> DropdownNGSMCConfigOptionBuilder<T> fluentBuilder(T value, Component name, List<T> options) {
		return new DropdownNGSMCConfigOptionBuilder<T>(value, name, options);
	}

	public static class DropdownNGSMCConfigOptionBuilder<T> extends AbstractFluentNGSMCConfigOptionBuilder<T, DropdownNGSMCConfigOptionBuilder<T>> {

		protected List<T> options;
		protected Function<T, Component> valueText = value -> Component.literal(String.valueOf(value));

		protected DropdownNGSMCConfigOptionBuilder(T value, Component name, List<T> options) {
			super(value, name);
			this.options = List.copyOf(options);
		}

		public DropdownNGSMCConfigOptionBuilder<T> setOptions(List<T> options) {
			this.options = List.copyOf(options);
			return this;
		}

		@SafeVarargs
		public final DropdownNGSMCConfigOptionBuilder<T> setOptions(T... options) {
			return setOptions(Arrays.asList(options));
		}

		public DropdownNGSMCConfigOptionBuilder<T> setValueText(Function<T, Component> valueText) {
			this.valueText = Objects.requireNonNull(valueText);
			return this;
		}

		@Override
		public DropdownNGSMCConfigOption<T> build() {
			if (options.isEmpty())
				throw new IllegalStateException("Dropdown options cannot be empty");
			if (!options.contains(value))
				throw new IllegalStateException("The current dropdown value must be present in its options");
			if (!options.contains(defaultValue))
				throw new IllegalStateException("The default dropdown value must be present in its options");
			return new DropdownNGSMCConfigOption<T>(defaultValue, value, name, saveOperation, validator, options, valueText);
		}

		@Override
		protected DropdownNGSMCConfigOptionBuilder<T> self() {
			return this;
		}
	}
}
