package io.github.ngspace.hudder.config;

import java.io.File;

import io.github.ngspace.hudder.Hudder;

public class ConfigManager {private ConfigManager() {}
	private static ConfigInfo config = new ConfigInfo(new File(ConfigInfo.FOLDER + "hud.json"));
	public static ConfigInfo getConfig() {return config;}
	public static void setConfig(ConfigInfo config) {ConfigManager.config=config;Hudder.config=config;}
}
