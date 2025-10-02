package dev.ngspace.ngsmcconfig.options;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.api.AbstractNGSMCConfigOptionBuilder;
import net.minecraft.network.chat.Component;

public class IntNGSMCConfigOption extends AbstractTextFieldNGSMCConfigOption<Integer> {

	protected boolean validnum = true;
	protected String invalidnum;
	
	protected IntNGSMCConfigOption(Integer defaultValue, Integer value, Component text, Consumer<Integer> saveOperation,
			Function<Integer, Component> validator) {
		super(defaultValue, value, text, saveOperation, validator);
		this.validator = val->{
			if (!validnum)
				return Component.literal("Invalid number: \"" + invalidnum + "\"");
			return validator==null?null:validator.apply(val);
		};
		textfilter = val -> {
			boolean passfilter = val.chars().allMatch(c->Character.isDigit(c)||c=='-');
			if (passfilter) {
				try {
					this.value = Integer.parseInt(val);
					validnum = true;
				} catch (NumberFormatException e) {
					validnum = false;
					invalidnum = val;
				}
			}
			return passfilter;
		};
	}
	
	public static AbstractNGSMCConfigOptionBuilder<Integer> builder(int value, Component name) {
		return new AbstractNGSMCConfigOptionBuilder<Integer>(value, name) {
			@Override public AbstractNGSMCConfigOption<Integer> build() {
				return new IntNGSMCConfigOption(defaultValue, value, name, saveOperation, validator);
			}
		};
	}
}
