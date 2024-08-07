package io.github.ngspace.hudder.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.CompileResult;
import io.github.ngspace.hudder.compilers.Compilers;
import io.github.ngspace.hudder.compilers.hudderv2.HudderV2Compiler;
import io.github.ngspace.hudder.util.HudFileUtils;
import net.minecraft.client.MinecraftClient;

public class ConfigInfo {
	
	/* EXPOSED :flushed: */
	@Expose public Map<String, Object> globalVariables = new HashMap<String, Object>();
    @Expose public String compilertype = "hudder";
	@Expose public String mainfile = "tutorial";
    @Expose public boolean enabled = true;
	@Expose public boolean shadow = true;
	@Expose public boolean showInF3 = false;
	@Expose public boolean javascript = false;
	@Expose public boolean globalVariablesEnabled = true;
	@Expose public float scale = 1f;
	@Expose public int color = 0xd6d6d6;
	@Expose public int yoffset = 1;
	@Expose public int xoffset = 1;
	@Expose public int lineHeight = 10;
	@Expose public int methodBuffer = 2;
	//V3.0.0
	@Expose public int backgroundcolor = 0x86353535;
	@Expose public boolean background = false;
	@Expose public boolean removegui = false;
	@Expose public boolean limitrate = true;
	/**
	 * @deprecated
	 * Please do not use this! use {@link io.github.ngspace.hudder.Hudder.IS_DEBUG}
	 */
	@Deprecated(forRemoval = false, since = "It's fucking creation")
	@Expose public boolean debug = false;
	
	public ATextCompiler compiler = new HudderV2Compiler();
	private File configFile = new File(HudFileUtils.FOLDER + "hud.json");
	
	public ConfigInfo(File f) {configFile = f;readConfig();}

	public CompileResult compile(String text) throws CompileException {
		if (compiler!=null) return compiler.compile(this, text);
		else throw new CompileException("There is no Compiler!");
	}
	public void readConfig() {
		try {
			if (!configFile.exists()) save();
		} catch (Exception e) {
			e.printStackTrace();
			return; //Cry about it but continue running.
		}
		try {
			Hudder.log("Reading Hudder config!");
			HudFileUtils.clearCache();
			String config = HudFileUtils.getFile(configFile.getName());
			Hudder.log("Loading Hudder Config File:\n" + config);
			Map<?,?> newinfo = new GsonBuilder().create().fromJson(config,HashMap.class);
			for(Field f : ConfigInfo.class.getFields())
				if (f.getAnnotation(Expose.class)!=null&&newinfo.get(f.getName())!=null) 
					setField(f, newinfo.get(f.getName()));
		} catch (Exception e) {
			e.printStackTrace();
			Hudder.log(e.getLocalizedMessage());
			Hudder.IS_DEBUG=true;//Failed to read config, turn on IS_DEBUG.
		}
		try {compiler = Compilers.getCompilerFromName(compilertype.toLowerCase());} 
		catch (Exception e) {compiler = new HudderV2Compiler();e.printStackTrace();}
	}
	private void setField(Field f, Object object) throws ReflectiveOperationException {
		if (object instanceof Number num) {
			if (f.getType().isAssignableFrom(int.class)) f.set(this, num.intValue());
			else if (f.getType().isAssignableFrom(float.class)) f.set(this, num.floatValue());
			else if (f.getType().isAssignableFrom(double.class)) f.set(this, num.doubleValue());
			else if (f.getType().isAssignableFrom(long.class)) f.set(this, num.longValue());
			else if (f.getType().isAssignableFrom(byte.class)) f.set(this, num.byteValue());
			else if (f.getType().isAssignableFrom(short.class)) f.set(this, num.shortValue());
			else f.set(this, object);
		} else f.set(this, object);
	}
	public void save() throws IOException {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			if (!configFile.createNewFile()) throw new IOException("Failed to create Hudder config file.");
		}
		try (FileWriter fw = new FileWriter(configFile)) {fw.append(gson.toJson(this));fw.flush();}
		catch (IOException e) {e.printStackTrace();Hudder.IS_DEBUG=true;}//Failed to save config, turn on IS_DEBUG.
	}
	public boolean shouldDrawResult(MinecraftClient ins) {
		return !ins.options.hudHidden&&(!ins.getDebugHud().shouldShowDebugHud()||showInF3)&&enabled;
	}
	public boolean shouldCompile(MinecraftClient ins) {
		return enabled&&ins.player!=null;
	}
}