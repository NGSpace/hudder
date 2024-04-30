package io.github.ngspace.hudder.config;

import java.io.File;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.util.HudFileUtils;

public class ConfigManager {private ConfigManager() {}
	private static ConfigInfo config = null;
	public static ConfigInfo getConfig() {
		if (config==null) config = new ConfigInfo(new File(HudFileUtils.FOLDER + "hud.json"));
		return config;
	}
	public static void setConfig(ConfigInfo config) {ConfigManager.config=config;Hudder.config=config;}
}
