package dev.ngspace.ngsmcconfig.api;

import java.util.List;

import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import net.minecraft.network.chat.Component;

public record NGSMCConfigCategory(Component title, List<AbstractNGSMCConfigOption<?>> options, NGSMCConfigIcon icon) {
	
	/**
	 * Backwards-compatible constructor for categories without an icon.
	 */
	public NGSMCConfigCategory(Component title, List<AbstractNGSMCConfigOption<?>> options) {
		this(title, options, null);
	}

	
	public NGSMCConfigCategory addOption(AbstractNGSMCConfigOption<?> option) {
		options.add(option);
		return this;
	}
	
}
