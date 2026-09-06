package dev.ngspace.ngsmcconfig.options;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigOptionBuilder;
import net.minecraft.network.chat.Component;

public class StringNGSMCConfigOption extends AbstractTextFieldNGSMCConfigOption<String> {
	
	protected StringNGSMCConfigOption(String defaultValue, String value, Component text, Consumer<String> saveOperation,
			Function<String, Component> validator, Function<String, Component> warning) {
		super(defaultValue, value, text, saveOperation, validator, warning);
		textfilter = val->{
			this.value = val;
			return true;
		};
	}

	public static NGSMCConfigOptionBuilder<String> builder(String value, Component name) {
	    return new NGSMCConfigOptionBuilder<String>(value, name) {
	        @Override public AbstractNGSMCConfigOption<String> build() {
	            return new StringNGSMCConfigOption(defaultValue, value, name, saveOperation, validator, warning);
	        }
	    };
	}
}
