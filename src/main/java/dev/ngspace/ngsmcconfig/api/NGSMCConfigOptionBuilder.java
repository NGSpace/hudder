package dev.ngspace.ngsmcconfig.api;

import net.minecraft.network.chat.Component;

public abstract class NGSMCConfigOptionBuilder<T> extends AbstractFluentNGSMCConfigOptionBuilder<T,
		NGSMCConfigOptionBuilder<T>> {

	protected NGSMCConfigOptionBuilder(T value, Component name) {
		super(value, name);
	}
	
	@Override
    protected NGSMCConfigOptionBuilder<T> self() {
		return this;
    }
	
}
