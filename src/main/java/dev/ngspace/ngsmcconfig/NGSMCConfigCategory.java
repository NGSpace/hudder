package dev.ngspace.ngsmcconfig;

import java.util.List;

import net.minecraft.network.chat.Component;

public record NGSMCConfigCategory(String name, Component title, List<AbstractNGSMCConfigOption<?>> options) {
	
	public void addOption(AbstractNGSMCConfigOption<?> option) {
		options.add(option);
	}
	
}
