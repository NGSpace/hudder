package dev.ngspace.hudder.api.compilers.interfaces;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;

public interface SettingsProvider {
	/**
	 * Sets up the configurable settings used by HUDs compiled by this compiler.
	 *
	 * @param hudsettings the configuration category in which HUD settings are set up
	 * @return {@code true} if the HUD settings were set up successfully;
	 *         {@code false} otherwise
	 */
	public abstract boolean setupHudSettings(NGSMCConfigCategory hudsettings);
}
