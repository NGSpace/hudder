package dev.ngspace.ngsmcconfig;

import java.awt.Component;

public abstract class AbstractNGSMCConfigOption<T> {

	public T defaultValue;
	public T value;
	public Component text;

	protected AbstractNGSMCConfigOption(T defaultValue, T value, Component text) {
		this.defaultValue = defaultValue;
		this.value = value;
		this.text = text;
	}
	
}
