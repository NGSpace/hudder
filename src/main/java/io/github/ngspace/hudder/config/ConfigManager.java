package io.github.ngspace.hudder.config;

import java.io.File;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.utils.HudFileUtils;

/**
 * @deprecated
 */
@Deprecated(forRemoval = true, since = "7.0.0+")
public class ConfigManager {private ConfigManager() {}
	private static HudderConfig config = null;
	/**
	 * @deprecated
	 */
    @Deprecated(forRemoval = true, since = "7.0.0+")
	public static HudderConfig getConfig() {
		if (config==null) config = new HudderConfig(new File(HudFileUtils.FOLDER + "hud.json"));
		return config;
	}
	public static void setConfig(HudderConfig config) {ConfigManager.config=config;Hudder.config=config;}
}
