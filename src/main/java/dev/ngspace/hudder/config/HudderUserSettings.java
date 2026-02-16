package dev.ngspace.hudder.config;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

public class HudderUserSettings {
	@Expose public Map<String, Object> globalVariables = new HashMap<String, Object>();
	@Expose public Map<String, Object> savedVariables = new HashMap<String, Object>();
	@Expose public String mainfile = "tutorial.hud";//Set "tutorial.hud" as the initial file selected
	@Expose public boolean enabled = true;
	@Expose public boolean shadow = true;
	@Expose public boolean showInF3 = false;
	@Expose public boolean unsafeoperations = false;
	@Expose public boolean globalVariablesEnabled = true;
	@Expose public float scale = 1f;
	@Expose public int color = 0xFFd6d6d6;
	@Expose public int yoffset = 1;
	@Expose public int xoffset = 1;
	@Expose public int lineHeight = 10;
	@Expose public int methodBuffer = 2;
	@Expose public int backgroundcolor = 0x86353535;
	@Expose public boolean background = true;
	@Expose public boolean removegui = false;
	@Expose public boolean limitrate = true;
	
	@Expose public int config_version = HudderConfig.HUDDER_CONFIG_VERSION;

    @Expose public String compilername = "hudder";
	
}
