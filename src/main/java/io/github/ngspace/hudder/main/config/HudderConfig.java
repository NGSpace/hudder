package io.github.ngspace.hudder.main.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.HudderV2Compiler;
import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.utils.HudFileUtils;
import io.github.ngspace.hudder.utils.testing.HudderUnitTester;
import net.minecraft.client.Minecraft;
import net.minidev.json.JSONObject;

public class HudderConfig {
	
	public static final int HUDDER_CONFIG_VERSION = 2;
	
	/* EXPOSED :flushed: */
	@Expose public Map<String, Object> globalVariables = new HashMap<String, Object>();
    @Expose public String compilertype = "hudder";
	@Expose public String mainfile = "tutorial";//Set "tutorial" as the default file selected
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
	
	@Expose public int config_version = HUDDER_CONFIG_VERSION;
	
	
	
	private ATextCompiler compiler = new HudderV2Compiler();
	private File configFile = new File(HudFileUtils.FOLDER + "hud.json");
	
	
	
    public HudderUnitTester hudderTester = new HudderUnitTester(new HudderV2Compiler());
    public Minecraft mc = Minecraft.getInstance();
	
    
    /**
     * Initalize the config. 
     * @param configFile - the config file.
     */
	public HudderConfig(File configFile) {
		this.configFile = configFile;
		readConfig();
	}


	/**
	 * Compiles the main hud using the selected compiler
	 * @return The result of the execution
	 * @throws CompileException
	 * @throws IOException
	 */
	public HudInformation compileMainHud() throws CompileException {
		if (getCompiler()!=null) return getCompiler().compile(this, HudFileUtils.readFile(mainfile), mainfile);
		else throw new CompileException("There is no Compiler!");
	}
	
	
	
	/**
	 * Read the JSON values from the config file that was provided durinng the ConfigInfo's initalization and apply
	 * them to this ConfigInfo Object.
	 */
	public void readConfig() {
		try {
			if (!configFile.exists()) {
				save();
				return; // We already know it'll be default value, no need to waste resources.
			}
		} catch (Exception e) {
			Hudder.IS_DEBUG=true;
			Hudder.log("Failed to create Hudder config file, falling back to default and enabling debug mode.");
			e.printStackTrace();
			return;
		}
		try {
			Hudder.log("Reading Hudder config!");
			String config = HudFileUtils.readFileWithoutCache(configFile.getName());
			Hudder.log("Loading Hudder Config File:\n" + config);
			Map<?,?> newinfo = new GsonBuilder().create().fromJson(config,HashMap.class);
			
			if (newinfo.containsKey("debug")) Hudder.IS_DEBUG = (boolean) newinfo.get("debug");
			if (!newinfo.containsKey("config_version")) config_version = 0;
			
			for(Field f : HudderConfig.class.getFields()) {
				if (f.getAnnotation(Expose.class)!=null&&newinfo.get(f.getName())!=null) {
					setField(f, newinfo.get(f.getName()));
				}
			}
			
			if (config_version<HUDDER_CONFIG_VERSION) {
				updateConfigFromVersion(config_version, newinfo);
				config_version = HUDDER_CONFIG_VERSION;
				save();
			}
			
		} catch (IOException e) {
			Hudder.IS_DEBUG=true;
			Hudder.log("Failed to read Hudder config file, enabling debug mode.");
			e.printStackTrace();
		} catch (ReflectiveOperationException e) {
			Hudder.IS_DEBUG=true;
			Hudder.log("Failed to set Hudder config values, enabling debug mode.");
			e.printStackTrace();
		}
		refreshCompiler();
	}
	
	
	
    private void updateConfigFromVersion(int version, Map<?, ?> newinfo) {
		if (version<1 && ((color >> 24) & 0xFF)==0) {
        	color = (255 << 24) | color;
        }
		if (version<2) {
			unsafeoperations = (boolean) newinfo.get("javascript");
		}
	}


	public static int compareSemanticVersions(String version1, String version2) {
        List<Integer> version1Components = Arrays.stream(version1.split("\\.")).map(Integer::parseInt).toList();
        List<Integer> version2Components = Arrays.stream(version2.split("\\.")).map(Integer::parseInt).toList();

        int maxLength = Math.max(version1Components.size(), version2Components.size());

        for (int i = 0; i < maxLength; i++) {
            int v1Component = i < version1Components.size() ? version1Components.get(i) : 0;
            int v2Component = i < version2Components.size() ? version2Components.get(i) : 0;

            if (v1Component > v2Component) {
                return 1;
            } else if (v1Component < v2Component) {
                return -1;
            }
        }

        return 0;
    }
    
	
	
	/**
	 * Sets the loaded compiler to match the name of the compiler set in {@code compilertype}
	 * <br><br>
	 * If unable to retrieve the compiler, switches to the default {@code HudderV2Compiler} instead.
	 */
	public void refreshCompiler() {
		try {
			compiler = Compilers.getCompilerFromName(compilertype.toLowerCase());
		} catch (Exception e) {
			compiler = new HudderV2Compiler();
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Sets the value of the provided field with type safety
	 */
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
	
	
	/**
	 * Saves the information on this config to the file that was provided during the ConfigInfo Object's
	 * initalizaiton.
	 * @throws IOException When fails to write to the file
	 */
	public void save() throws IOException {
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			if (!configFile.createNewFile()) throw new IOException("Failed to create Hudder config file.");
		}
		try (FileWriter config_writer = new FileWriter(configFile)) {
			JSONObject json_output = new JSONObject();
			for (Field f : HudderConfig.class.getFields())
				if (f.getAnnotation(Expose.class)!=null)
					json_output.put(f.getName(), f.get(this));

			json_output.put("debug", Hudder.IS_DEBUG);
			
			config_writer.append(new GsonBuilder().setPrettyPrinting().create().toJson(json_output));
			config_writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			Hudder.IS_DEBUG=true;
			throw e;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			Hudder.IS_DEBUG=true;
		}
	}
	
	
	
	/**
	 * Whether Hudder should draw it's hud or not
	 * @return true or false
	 */
	public boolean shouldDrawResult() {
		return !mc.options.hideGui&&(!mc.getDebugOverlay().showDebugScreen()||showInF3)&&enabled;
	}
	
	

	/**
	 * Whether Hudder should compile the hudder file or not
	 * @return true or false
	 */
	public boolean shouldCompile() {
		return enabled&&mc.player!=null;
	}
	
	
	/**
	 * Sets the compilertype to the provided compiler name and refreshes the compiler
	 * @param compilername - the name of the compiler
	 * @return the provided name (for clothconfig)
	 */
	public String setCompiler(String compilername) {
		compilertype=compilername;
		refreshCompiler();
		return compilername;
	}

	
	/**
	 * Returns the compiler currently used to compile the main file.
	 * @return The current compiler
	 */
	public ATextCompiler getCompiler() {
		return compiler;
	}
}