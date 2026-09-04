package dev.ngspace.hudder.config;

import java.io.IOException;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.CompilerRegistry;
import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.utils.CompilerEntry;
import dev.ngspace.hudder.defaultcompilers.HudPackCompiler;
import dev.ngspace.hudder.defaultcompilers.HudderV2Compiler;
import dev.ngspace.hudder.defaultcompilers.HudderV3Compiler;
import dev.ngspace.hudder.defaultcompilers.JavaScriptCompiler;
import dev.ngspace.hudder.main.HudCompilationManager;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.utils.NoAccess;
import net.minecraft.client.Minecraft;

public class HudderConfig {
	
	public static final int HUDDER_CONFIG_VERSION = 6;
	
	public final HudderUserSettings userSettings = new HudderUserSettings();

	public final HudCompilationManager compilationManager;
	
	public final HudderV2Compiler hudderV2Compiler;
	public final HudderV3Compiler hudderV3Compiler;
	public final HudPackCompiler hudpackCompiler;
	public final JavaScriptCompiler javaScriptCompiler;

	public final CompilerRegistry registry;
	private @Nullable AHudCompiler<?> compiler;
	private final Path configFile;

	private boolean first_run;
	
	private static final Minecraft mc = Minecraft.getInstance();
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
    
    /**
     * Initalize the config. 
     * @param configFile - the config file.
     * @throws IOException 
     */
	public HudderConfig(Path configFile, CompilerRegistry registry) throws IOException {
		compilationManager = new HudCompilationManager(this, registry);
		this.configFile = configFile;
		if (!Files.exists(configFile)) {
			Path oldconfigloc = HudFileUtils.FOLDER.resolve("hud.json");
			if (Files.exists(oldconfigloc)) {
				Hudder.log("Migrating Hudder config");
				Files.move(configFile, oldconfigloc);
			}
		}
		this.hudderV2Compiler = new HudderV2Compiler(this);
		registry.registerCompiler("hudderv2", "Hudder V2", false, false, hudderV2Compiler);
		this.hudderV3Compiler = new HudderV3Compiler(this);
		registry.registerCompiler("hudder", "Hudder V3", false, false, hudderV3Compiler);
		this.hudpackCompiler = new HudPackCompiler(this);
		registry.registerCompiler("pack", "Hudpack", false, false, hudpackCompiler);
		this.javaScriptCompiler = new JavaScriptCompiler(this);
		registry.registerCompiler("js", "JavaScript", false, false, javaScriptCompiler);
		this.compiler = hudderV3Compiler;
		this.registry = registry;
		readAndUpdateConfig();
	}

	/**
	 * Read the JSON values from the config file that was provided durinng the ConfigInfo's initalization and apply
	 * them to this ConfigInfo Object.
	 */
	public void readAndUpdateConfig() {
		try {
			if (!Files.exists(configFile)) {
				first_run = true;
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
			Type type = new TypeToken<Map<String, JsonElement>>() {}.getType();
			Map<String, JsonElement> newinfo = gson.fromJson(config, type);
			
			if (newinfo.containsKey("debug"))
				Hudder.IS_DEBUG = newinfo.get("debug").getAsBoolean();
			if (!newinfo.containsKey("config_version"))
				userSettings.config_version = 0;
			
			for(Field field : HudderUserSettings.class.getFields()) {
				if (field.getAnnotation(Expose.class) == null)
			        continue;
				
			    JsonElement element = newinfo.get(field.getName());

			    if (element != null && !element.isJsonNull()) {
			        Object value = gson.fromJson(element, field.getGenericType());
			        field.set(userSettings, value);
			    }
			}
			
			if (userSettings.config_version<HUDDER_CONFIG_VERSION) {
				updateConfigFromVersion(userSettings.config_version, newinfo);
				userSettings.config_version = HUDDER_CONFIG_VERSION;
				save();
			}
			
		} catch (IOException | JsonSyntaxException e) {
			Hudder.IS_DEBUG=true;
			Hudder.log("Failed to read Hudder config file, enabling debug mode.");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Hudder.IS_DEBUG=true;
			Hudder.log("Failed to set Hudder config values, enabling debug mode.");
			e.printStackTrace();
		}
		refreshCompiler();
	}
	
	
	
	private void updateConfigFromVersion(int version, Map<String, JsonElement> newinfo) {
		if (version<1 && ((userSettings.color >> 24) & 0xFF)==0) {
			userSettings.color = (255 << 24) | userSettings.color;
        }
		var js = newinfo.get("javascript");
		if (version<2&&js!=null&&js.isJsonPrimitive()&&js.getAsJsonPrimitive().isBoolean()) {
			userSettings.unsafeoperations = newinfo.get("javascript").getAsBoolean();
		}
		if (version<3&&newinfo.containsKey("compilertype")) {
			userSettings.compilername = switch (newinfo.get("compilertype").getAsString()) {
				case "none","null" -> "empty";
				case "javascript" -> "js";
				case "default", "defaultcompiler", "default compiler" -> "hudder";
				default -> newinfo.get("compilertype").getAsString();
			};
		}
		/*
		 * Until version 6, when migrating from version 3 or earlier used to rename the default
		 * hudder huds to contain a .hud file extension (even going as far as rewriting user huds
		 * to point to the newly renamed file). While working on some of the migration code
		 * I realized that doing this to "claim" a specific file extension is stupid
		 * and way too dangerous for what it's worth. Also doesn't help that:
		 * 1. The rewriting of user huds only rewrote Hudder huds (Not javascript huds, meaning they point to
		 *  the old filepaths)
		 * 2. It served no functional purpose
		 * 3. Newer Hudder shows a proper warning in the config screen for incorrect file extensions
		 * The migration code was useful when it was added but it had served it's purpose and keeping it
		 * was too much of a burden so I decided to get rid it.
		 */
		if (version<5) {
			var xoffset = newinfo.get("xoffset");
			var yoffset = newinfo.get("yoffset");
			if (xoffset!=null&&!xoffset.isJsonNull()) {
				userSettings.xoffset_left = xoffset.getAsInt();
				userSettings.xoffset_right = xoffset.getAsInt();
			}
			if (yoffset!=null&&!yoffset.isJsonNull()) {
				userSettings.yoffset_top = yoffset.getAsInt();
				userSettings.yoffset_bottom = yoffset.getAsInt();
			}
		}
		// The default Hudder implementation is now v3 so the unique identifier "hudderv3" has been removed
		// and V3 has became simply "hudder"
		if (version<6&&newinfo.containsKey("compilername")
				&&"hudderv3".equals(newinfo.get("compilername").getAsString())) {
			userSettings.compilername = "hudder";
		}
	}
    
	
	
	/**
	 * Sets the loaded compiler to match the name of the compiler set in {@code compilertype}
	 * <br><br>
	 * If unable to retrieve the compiler, switches to the default {@code HudderV3Compiler} instead.
	 */
	public void refreshCompiler() {
		Optional<CompilerEntry> entry = registry.findEntryFromId(compilerId());
		if (entry.isPresent()) {
			compiler = entry.get().compiler();
		} else {
			Hudder.log("Couldn't find compiler \"" + compilerId() + "\".");
			compiler = null;
		}
	}
	
	
	/**
	 * Saves the information on this config to the file that was provided during the ConfigInfo Object's
	 * initalizaiton.
	 * @throws IOException When fails to write to the file
	 */
	public void save() throws IOException {
		if (!Files.exists(configFile)) {
			Files.createDirectories(configFile.getParent());
			Files.createFile(configFile);
		}
		try {
			Map<String, Object> json_output = new HashMap<String, Object>();
			for (Field f : HudderUserSettings.class.getDeclaredFields())
				if (f.getAnnotation(Expose.class)!=null)
					json_output.put(f.getName(), f.get(userSettings));

			json_output.put("debug", Hudder.IS_DEBUG);
			
			Files.writeString(configFile, gson.toJson(json_output));
		} catch (IOException e) {
			e.printStackTrace();
			Hudder.IS_DEBUG=true;
			throw e;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			Hudder.IS_DEBUG=true;
			throw new IOException(e);
		}
	}
	
	
	
	/**
	 * Whether Hudder should draw it's hud or not
	 * @return true or false
	 */
	public boolean shouldDrawResult() {
		return !mc.gui.hud.isHidden()&&(!mc.debugEntries.isOverlayVisible()||showInF3())&&enabled();
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
	public @Nullable AHudCompiler<?> getCompiler() {
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

	public Path mainfile() {
	    return HudFileUtils.FOLDER.resolve(mainfileString());
	}

	public String mainfileString() {
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

	public int yoffsetTop() {
	    return userSettings.yoffset_top;
	}

	public int yoffsetBottom() {
	    return userSettings.yoffset_bottom;
	}

	public int xoffsetLeft() {
	    return userSettings.xoffset_left;
	}

	public int xoffsetRight() {
	    return userSettings.xoffset_right;
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

	public boolean removeeffects() {
	    return userSettings.removeeffects;
	}

	public boolean removeBossBars() {
		return userSettings.removeBossBars;
	}

	public boolean limitrate() {
	    return userSettings.limitrate;
	}

	public boolean autorefresh() {
	    return userSettings.autorefresh;
	}
	
	public boolean disableHudpackVersionCheck() {
		return userSettings.disableHudpackVersionCheck;
	}

	public boolean disableWarnings() {
		return userSettings.disableWarnings;
	}

	public int configVersion() {
	    return userSettings.config_version;
	}

	public String compilerId() {
	    return userSettings.compilername;
	}
	
	public Map<String, Object> getHudSettings(String compiler, String hud) {
		return userSettings.hudSettings.computeIfAbsent(compiler,_->new HashMap<String, Map<String, Object>>())
				.computeIfAbsent(hud, _->new HashMap<String, Object>());
	}
	
	/**
	 * @deprecated
	 */
	@Deprecated(since = "9.2.0", forRemoval = true)
	public Map<String, Object> globalVariables() {
		return userSettings.globalVariables;
	}

	public boolean isFirstRun() {
		return first_run;
	}
	
}