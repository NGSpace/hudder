package dev.ngspace.ngsmcconfig.options;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.ngspace.ngsmcconfig.api.AbstractNGSMCConfigOptionBuilder;
import net.minecraft.network.chat.Component;

public class DoubleNGSMCConfigOption extends AbstractTextFieldNGSMCConfigOption<Double> {

	protected boolean validnum;
	protected String invalidnum;
	
	protected DoubleNGSMCConfigOption(double defaultValue, double value, Component text, Consumer<Double> saveOperation,
			Function<Double, Component> validator) {
		super(defaultValue, value, text, saveOperation, validator);
		this.validator = val->{
			if (!validnum)
				return Component.literal("Invalid number: \"" + invalidnum + "\"");
			return validator==null?null:validator.apply(val);
		};
		textfilter = val -> {
			boolean passfilter = val.chars().allMatch(c->Character.isDigit(c)||c=='-'||c=='.');
			if (passfilter) {
				try {
					this.value = Double.parseDouble(val);
					validnum = true;
				} catch (NumberFormatException e) {
					validnum = false;
					invalidnum = val;
				}
			}
			return passfilter;
		};
	}
	
	public static AbstractNGSMCConfigOptionBuilder<Double> builder(double value, Component name) {
		return new AbstractNGSMCConfigOptionBuilder<Double>(value, name) {
			@Override public AbstractNGSMCConfigOption<Double> build() {
				return new DoubleNGSMCConfigOption(defaultValue, value, name, saveOperation, validator);
			}
		};
	}
}