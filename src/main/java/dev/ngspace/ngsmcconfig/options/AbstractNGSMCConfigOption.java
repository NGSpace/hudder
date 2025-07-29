package dev.ngspace.ngsmcconfig.options;

import dev.ngspace.ngsmcconfig.NGSMCConfigEntry;
import net.minecraft.network.chat.Component;

public abstract class AbstractNGSMCConfigOption<T> {

	public T defaultValue;
	public T value;
	public Component text;

	protected AbstractNGSMCConfigOption(T defaultValue, T value, Component text) {
		this.defaultValue = defaultValue;
		this.value = value;
		this.text = text;
	}
	
	public abstract NGSMCConfigEntry buildEntry();
	
	public abstract static class Builder<T> {
		
		protected int value;
		protected int defaultValue;
		protected Component name;

		protected Builder(int value, Component name) {
			this.value = value;
			this.defaultValue = value;
			this.name = name;
		}
		
		public Builder<T> setValue(int value) {
			this.value = value;
			return this;
		}

		public Builder<T> setDefaultValue(int defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Builder<T> setName(Component name) {
			this.name = name;
			return this;
		}

		public abstract AbstractNGSMCConfigOption<T> build();
	}
}
