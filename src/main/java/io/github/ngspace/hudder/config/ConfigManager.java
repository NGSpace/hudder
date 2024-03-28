package io.github.ngspace.hudder.config;

import java.io.File;

public class ConfigManager {private ConfigManager() {}
	private static ConfigInfo conf = new ConfigInfo(new File(ConfigInfo.FOLDER + "hud.json"));
	public static ConfigInfo getConfig() {return conf;}
}
