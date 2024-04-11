package io.github.ngspace.hudder.config;

import static java.io.File.separator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.CachedTextReader;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.CompileResult;
import io.github.ngspace.hudder.compilers.DefaultCompiler;
import net.fabricmc.loader.api.FabricLoader;
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
	@Expose public int metaBuffer = 2;
//	@Expose public int backgroundcolor = 0xd6d6d6;
//	@Expose public boolean background = false;
	
	public CachedTextReader textReader = new CachedTextReader();
	public ATextCompiler compiler = new DefaultCompiler();
	private File configFile = new File(ConfigInfo.FOLDER + "hud.json");
	
	private static final Map<String, ATextCompiler> loadedcomps = new HashMap<String,ATextCompiler>();
	
	public static final String FABRIC_CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().toString();
	public static final String FOLDER = FABRIC_CONFIG_FOLDER + separator + Hudder.MOD_ID + separator;
	public ConfigInfo(File f) {configFile = f;readConfig();}
	
	public String getFile(String file) {return textReader.getFile(FOLDER + file);}
	public boolean readFile(String file) {return textReader.readFile(FOLDER + file);}

	public CompileResult compile(String text) throws CompileException {
		if (compiler!=null) return compiler.compile(this, text);
		else throw new CompileException("There is no Compiler!");
	}
	
	public void readConfig() {
		try {
			Hudder.log("Reading Hudder config!");
			textReader.readFile(configFile.getAbsolutePath());
			String config = textReader.getFile(configFile.getAbsolutePath());
			Hudder.log("Loading Hudder Config File:\n" + config);
			Map<String, Object> newinfo = new GsonBuilder().create().fromJson(config,
					new TypeToken<Map<String, Object>>(){}.getType());
			for(Field f : ConfigInfo.class.getFields())
				if (f.getAnnotation(Expose.class)!=null&&newinfo.get(f.getName())!=null) 
					setField(f, newinfo.get(f.getName()));
		} catch (Exception e) {
			e.printStackTrace();
			Hudder.log(e.getLocalizedMessage());
		} finally {
			try {compiler = getCompilerFromName(compilertype.toLowerCase());} 
			catch (Exception e) {compiler = new DefaultCompiler();if (Hudder.IS_DEBUG) e.printStackTrace();}
		}
	}
	private void setField(Field f, Object object) throws Exception {
		if (object instanceof Number num) {
			if (f.getType().isAssignableFrom(Integer.class)) f.set(this, (int)num);
			else if (f.getType().isAssignableFrom(Double.class)) f.set(this, (double)num);
			else if (f.getType().isAssignableFrom(Float.class)) f.set(this, (float)num);
			else if (f.getType().isAssignableFrom(Long.class)) f.set(this, (long)num);
		} else f.set(this, object);
	}

	public void saveConfig() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		try (FileWriter fw = new FileWriter(configFile)) {fw.append(gson.toJson(this));fw.flush();}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public ATextCompiler getCompilerFromName(String name) throws Exception {
		String comp = name.toLowerCase();
		if (ATextCompiler.compilers.get(comp)==null) return getCompilerFromName("default");
		if (!loadedcomps.containsKey(comp)) loadedcomps.put(comp,(ATextCompiler) Class.forName
				(ATextCompiler.compilers.get(comp)).getDeclaredConstructor().newInstance());
		return loadedcomps.get(comp);
	}

	public boolean shouldDrawResult(MinecraftClient ins) {
		return !ins.options.hudHidden
				&&(!ins.getDebugHud().shouldShowDebugHud()||showInF3)
				&&enabled;
	}
}
