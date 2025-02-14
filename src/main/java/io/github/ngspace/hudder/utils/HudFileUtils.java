package io.github.ngspace.hudder.utils;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.mojang.blaze3d.platform.NativeImage;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

public class HudFileUtils {private HudFileUtils() {}

	private static CachedReader reader = new CachedReader();
	private static List<ERunnable<CompileException>> chacheClearRunnables = new ArrayList<ERunnable<CompileException>>();
	
	static {addClearFileCacheListener(reader);}

	public static final String FABRIC_CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().toString();
	public static final String FOLDER = FABRIC_CONFIG_FOLDER + separator + "hudder" + separator;
    public static final String ASSETS = "/assets/hudder/";
    public static final String[] DEFAULT_HUDS = {"tutorial", "hand", "armor", "armorside", "hud", "basic", "hud.js",
    		"hotbar.js", "fibonacci", "worldtime.js"};
    public static final String[] DEFAULT_TEXTURES = {"pointer.png","selection.png"};
	
    
    
    /**
     * Read file to String
     * @param file - the file to read
     * @return The text in the file
     * @throws IOException
     */
	public static String getFile(String file) throws IOException {
		return reader.getFile(sanitize(FOLDER + file));
	}
	
	
	
	/**
	 * Reads the provided the image and loads it at the provided texture id
	 * <br><br>
	 * INPUT IS SANITIZED
	 * 
	 * @param file - The location of the image relative to the Hudder (SANITIZED)
	 * @param id - The ID the image should be loaded into
	 * @return the image that was loaded
	 * @throws IOException
	 */
	public static NativeImage getAndRegisterImage(String file, ResourceLocation id) throws IOException {
		return reader.getAndRegisterImage(sanitize(FOLDER + file), id);
	}
	
	

	/**
	 * Calls all filecache listners.
	 * @throws CompileException - if failed to clear cache.
	 */
	public static void clearFileCache() throws CompileException {
		for(ERunnable<CompileException> r : chacheClearRunnables) r.run();
	}
	/**
	 * Triggers when clearFileCache() is called (usually when the user modifies a hud)
	 * @param listener - the Runnable to trigger
	 */
	public static void addClearFileCacheListener(ERunnable<CompileException> listener) {
		chacheClearRunnables.add(listener);
	}
	
	
	/**
	 * Checks if a filename is dirty or not.
	 * @param f - The name of the file
	 * @return the filename provided
	 * @throws SecurityException - If the provided filename is "dirty"
	 */
	public static String sanitize(String f) throws SecurityException {
		if (!new File(f).getAbsolutePath().startsWith(FOLDER)) throwError(f);
		int j = 0;
		int k = 0;
		for (int i = 0;i<f.length();i++) {
			char c = f.charAt(i);
			if (c=='.') j++;
			else if (c=='/'||c=='\\') {
				if (j==2&&k==0) throwError(f);
				k = 0;
			} else {j = 0;k++;}
		}
		return f;
	}
	
	
	
	/**
	 * Throw fake IO error.
	 * @param file - the filename to add to the error
	 */
	private static final void throwError(String file) {
		throw new SecurityException(file + " (No such file or directory)");
	}
	
	
	
	/**
	 * Sanitizes the provided relative path and then checks if it exists
	 * @param file - the path
	 * @return whether the file exists or not
	 */
	public static boolean exists(String file) {
		return new File(sanitize(FOLDER + file)).exists();
	}
	
	
	
	/**
	 * Creates any missing default huds and default textures
	 */
	public static void makeDefaultHud() {

		// Add missing huds to Hudder config folder (Assume one exists)
		for (String file : DEFAULT_HUDS) {
			File dest = new File(FOLDER, file);
			if (dest.exists()) continue;
			try {FileUtils.copyURLToFile(HudFileUtils.class.getResource(ASSETS + file), dest);}
			catch (IOException e) {e.printStackTrace();}
		}
		
		// Create A Textures folder if missing
		if (!new File(FOLDER + "Textures").exists()) new File(FOLDER + "Textures").mkdir();
		
		// Add missing textures to Textures folder
		for (String file : DEFAULT_TEXTURES) {
			File dest = new File(FOLDER + "Textures", file);
			if (dest.exists()) continue;
			try {FileUtils.copyURLToFile(HudFileUtils.class.getResource(ASSETS+"Textures/" + file), dest);}
			catch (IOException e) {e.printStackTrace();}
		}
	}
	
	public static void reload() {
		
	}
}