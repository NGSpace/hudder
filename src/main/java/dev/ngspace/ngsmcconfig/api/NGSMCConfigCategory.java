package dev.ngspace.ngsmcconfig.api;

import java.util.List;

import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import net.minecraft.network.chat.Component;

public record NGSMCConfigCategory(Component title, List<AbstractNGSMCConfigOption<?>> options) {
	
	public NGSMCConfigCategory addOption(AbstractNGSMCConfigOption<?> option) {
		options.add(option);
		return this;
	}
	
}
