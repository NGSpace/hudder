package dev.ngspace.ngsmcconfig.options;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.api.AbstractNGSMCConfigOptionBuilder;
import net.minecraft.network.chat.Component;

public class StringNGSMCConfigOption extends AbstractTextFieldNGSMCConfigOption<String> {
	
	protected StringNGSMCConfigOption(String defaultValue, String value, Component text, Consumer<String> saveOperation,
			Function<String, Component> validator) {
		super(defaultValue, value, text, saveOperation, validator);
		textfilter = val->{
			this.value = val;
			return true;
		};
	}

	public static AbstractNGSMCConfigOptionBuilder<String> builder(String value, Component name) {
		return new AbstractNGSMCConfigOptionBuilder<String>(value, name) {
			@Override public AbstractNGSMCConfigOption<String> build() {
				return new StringNGSMCConfigOption(defaultValue, value, name, saveOperation, validator);
			}
		};
	}
}
