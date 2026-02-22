package dev.ngspace.hudder.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.HudderV2Compiler;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.Compilers;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.utils.NoAccess;
import dev.ngspace.hudder.utils.testing.HudderUnitTester;
import net.minecraft.client.Minecraft;

public class HudderConfig {
	
	public static final int HUDDER_CONFIG_VERSION = 4;
	public static final File DEFAULT_CONFIG_FILE = new File(HudFileUtils.FABRIC_CONFIG_FOLDER + File.separator + "hudder.json");
	
	public HudderUserSettings userSettings = new HudderUserSettings();
	
	
	
	private AHudCompiler<?> compiler = new HudderV2Compiler();
	private File configFile;
	
	
	
    public HudderUnitTester hudderTester = new HudderUnitTester(new HudderV2Compiler());
    public Minecraft mc = Minecraft.getInstance();
	
    
    /**
     * Initalize the config. 
     * @param configFile - the config file.
     */
	public HudderConfig(File configFile) {
		this.configFile = configFile;
		if (!configFile.exists()) {
			File oldconfigloc = new File(HudFileUtils.FOLDER + "hud.json");
			if (oldconfigloc.exists()) {
				Hudder.log("Migrating Hudder config");
				if (!oldconfigloc.renameTo(configFile)) {
					Hudder.log("Failed to migrate Hudder config file.");
					throw new UnsupportedOperationException("Failed to migrate Hudder config file.");
				}
			}
		}
		readAndUpdateConfig();
	}


	/**
	 * Compiles the main hud using the selected compiler
	 * @return The result of the execution
	 * @throws CompileException
	 * @throws IOException
	 */
	public HudInformation compileMainHud() throws CompileException {
		if (getCompiler()!=null) return getCompiler().processAndCompile(this, mainfile(), mainfile());
		else throw new CompileException("There is no Compiler!");
	}


	/**
	 * Read the JSON values from the config file that was provided durinng the ConfigInfo's initalization and apply
	 * them to this ConfigInfo Object.
	 */
	public void readAndUpdateConfig() {
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
			Hudder.log("Loading Hudder config");
			String config = HudFileUtils.readFileUnsanitized(configFile);
			Map<?,?> newinfo = new GsonBuilder().create().fromJson(config,HashMap.class);
			
			if (newinfo.containsKey("debug")) Hudder.IS_DEBUG = (boolean) newinfo.get("debug");
			if (!newinfo.containsKey("config_version")) userSettings.config_version = 0;
			
			for(Field f : HudderUserSettings.class.getDeclaredFields()) {
				if (f.getAnnotation(Expose.class)!=null&&newinfo.get(f.getName())!=null) {
					setField(f, newinfo.get(f.getName()));
				}
			}
			
			if (userSettings.config_version<HUDDER_CONFIG_VERSION) {
				updateConfigFromVersion(userSettings.config_version, newinfo);
				userSettings.config_version = HUDDER_CONFIG_VERSION;
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
		if (version<1 && ((userSettings.color >> 24) & 0xFF)==0) {
			userSettings.color = (255 << 24) | userSettings.color;
        }
		if (version<2) {
			userSettings.unsafeoperations = (boolean) newinfo.get("javascript");
		}
		if (version<3&&newinfo.get("compilertype")!=null) {
			userSettings.compilername = switch (newinfo.get("compilertype").toString()) {
				case "none","null" -> "empty";
				case "javascript" -> "js";
				case "default", "defaultcompiler", "default compiler" -> "hudder";
				default -> newinfo.get("compilertype").toString();
			};
		}
		if (version<4) {
			try {
				// For the love of god, back up the user's data before doing literally anything to it.
				FileUtils.copyDirectory(new File(HudFileUtils.FOLDER),
						new File(HudFileUtils.FABRIC_CONFIG_FOLDER + File.separator + "hudder_backup"), true);
				
				String[] oldBuiltins = new String[] {"tutorial", "hand", "armorside", "hud", "basic"};
				for (String name : oldBuiltins) {
					File f = new File(HudFileUtils.FOLDER + name);
					if (f.exists()) {
						String res = new String(Files.readAllBytes(f.toPath()));
						FileWriter writer = new FileWriter(f);
						for (String name2 : oldBuiltins) {
							res = res.replaceAll("; *run *, *[\"']?"+name2+"[\"']? *;",
									";run, \""+name2+".hud\";");
						}
						writer.append(res);
						writer.flush();
						writer.close();
						
						if (!f.renameTo(new File(HudFileUtils.FOLDER + name + ".hud"))) {
							Hudder.error("Failed to update old hud, stopping migration process.");
							break;
						}
					}
					
					if (userSettings.mainfile.equals(name))
						userSettings.mainfile = name + ".hud";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    
	
	
	/**
	 * Sets the loaded compiler to match the name of the compiler set in {@code compilertype}
	 * <br><br>
	 * If unable to retrieve the compiler, switches to the default {@code HudderV2Compiler} instead.
	 */
	public void refreshCompiler() {
		try {
			compiler = Compilers.getCompilerFromName(compilerName());
		} catch (Exception e) {
			e.printStackTrace();
			Hudder.log("Using default compiler due to error");
			compiler = new HudderV2Compiler();
		}
	}
	
	
	/**
	 * Sets the value of the provided field with type safety
	 */
	private void setField(Field f, Object object) throws ReflectiveOperationException {
		if (object instanceof Number num) {
			if (f.getType()==(int.class)) f.set(userSettings, num.intValue());
			else if (f.getType()==(float.class)) f.set(userSettings, num.floatValue());
			else if (f.getType()==(double.class)) f.set(userSettings, num.doubleValue());
			else if (f.getType()==(long.class)) f.set(userSettings, num.longValue());
			else if (f.getType()==(byte.class)) f.set(userSettings, num.byteValue());
			else if (f.getType()==(short.class)) f.set(userSettings, num.shortValue());
			else f.set(userSettings, object);
		} else f.set(userSettings, object);
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
			Map<String, Object> json_output = new HashMap<String, Object>();
			for (Field f : HudderUserSettings.class.getDeclaredFields())
				if (f.getAnnotation(Expose.class)!=null)
					json_output.put(f.getName(), f.get(userSettings));

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
		return !mc.options.hideGui&&(!mc.debugEntries.isOverlayVisible()||showInF3())&&enabled();
	}
	
	

	/**
	 * Whether Hudder should compile the hudder file or not
	 * @return true or false
	 */
	public boolean shouldCompile() {
		return enabled()&&mc.player!=null;
	}
	
	
	/**
	 * Sets the compilertype to the provided compiler name and refreshes the compiler
	 * @param compilername - the name of the compiler
	 * @return the provided name (for clothconfig)
	 */
	public String setCompilerName(String compilername) {
		userSettings.compilername=compilername;
		refreshCompiler();
		return compilername;
	}

	
	/**
	 * Returns the compiler currently used to compile the main file.
	 * @return The current compiler
	 */
	public AHudCompiler<?> getCompiler() {
		return compiler;
	}


	public static boolean isAccessible(Class<?> clazz) {
		return !clazz.accessFlags().contains(AccessFlag.PRIVATE)
				&&!clazz.isAnnotationPresent(NoAccess.class);
	}
	public static boolean isPublic(Member member) {
		return member.accessFlags().contains(AccessFlag.PUBLIC)&&!member.accessFlags().contains(AccessFlag.PRIVATE);
	}


	public void putSavedVariable(String key, Object value) throws IOException {
		if (!(value instanceof Number
				|| value instanceof String
				|| value instanceof Boolean
				|| value instanceof Character
				|| unsafeoperations()
				|| value==null))
			throw new IllegalArgumentException("Can only save variables of types: Number, String, Boolean or"
					+ " Character with unsafe operations disabled.");
		savedVariables().put(key, value);
		save();
	}
	
	public Map<String, Object> savedVariables() {
	    return userSettings.savedVariables;
	}

	public String mainfile() {
	    return userSettings.mainfile;
	}

	public boolean enabled() {
	    return userSettings.enabled;
	}

	public boolean shadow() {
	    return userSettings.shadow;
	}

	public boolean showInF3() {
	    return userSettings.showInF3;
	}

	public boolean unsafeoperations() {
	    return userSettings.unsafeoperations;
	}

	public boolean globalVariablesEnabled() {
	    return userSettings.globalVariablesEnabled;
	}

	public float scale() {
	    return userSettings.scale;
	}

	public int color() {
	    return userSettings.color;
	}

	public int yoffset() {
	    return userSettings.yoffset;
	}

	public int xoffset() {
	    return userSettings.xoffset;
	}

	public int lineHeight() {
	    return userSettings.lineHeight;
	}

	public int methodBuffer() {
	    return userSettings.methodBuffer;
	}

	public int backgroundcolor() {
	    return userSettings.backgroundcolor;
	}

	public boolean background() {
	    return userSettings.background;
	}

	public boolean removegui() {
	    return userSettings.removegui;
	}

	public boolean limitrate() {
	    return userSettings.limitrate;
	}

	public boolean autorefresh() {
	    return userSettings.autorefresh;
	}

	public int configVersion() {
	    return userSettings.config_version;
	}

	public String compilerName() {
	    return userSettings.compilername;
	}
	
	public Map<String, Object> getHudSettings(String compiler, String hud) {
		return userSettings.hudSettings.computeIfAbsent(compiler,k->new HashMap<String, Map<String, Object>>())
				.computeIfAbsent(hud, k->new HashMap<String, Object>());
	}
	
	/**
	 * @deprecated
	 * @return fuckall
	 */
	@Deprecated(since = "9.2.0", forRemoval = true)
	public Map<String, Object> globalVariables() {
		return userSettings.globalVariables;
	}
}