package io.github.ngspace.hudder.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

public class HudFileUtils {private HudFileUtils() {}

	private static CachedReader reader = new CachedReader();
	private static List<ERunnable<IOException>> reloadResourcesListeners = new ArrayList<ERunnable<IOException>>();

	public static final String FABRIC_CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().toString();
	public static final String FOLDER = FABRIC_CONFIG_FOLDER + File.separator + "hudder" + File.separator;
    public static final String ASSETS = "/assets/hudder/";
    public static final String[] DEFAULT_HUDS = {"tutorial", "hand", "armor", "armorside", "hud", "basic", "hud.js",
    		"hotbar.js", "fibonacci", "worldtime.js"};
    public static final String[] DEFAULT_TEXTURES = {"pointer.png","selection.png"};
	
    
    
    /**
     * Read file to String
     * @param file - the file to read
     * @return The text in the file
     */
	public static String readFile(String file) {
		return reader.getCachedFileAsString(sanitize(FOLDER + file));
	}
	
    
    
	public static byte[] readFileBytes(String file) {
		return reader.getCachedFile(sanitize(FOLDER + file));
	}
	
	
	
    /**
     * Read file to String
     * @param file - the file to read
     * @return The text in the file
     * @throws IOException
     */
	public static String readFileWithoutCache(String file) throws IOException {
		return new String(reader.reader.readFile(new File(sanitize(FOLDER + file))));
	}
	
	
	
	/**
	 * Triggers when clearFileCache() is called (usually when the user modifies a hud)
	 * @param listener - the Runnable to trigger
	 */
	public static void addReloadResourcesListener(ERunnable<IOException> listener) {
		reloadResourcesListeners.add(listener);
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
		if ("".equals(file)) return false;
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
			try {FileUtils.copyURLToFile(HudFileUtils.class.getResource(ASSETS + "huds/" + file), dest);}
			catch (IOException e) {e.printStackTrace();}
		}
		
		// Create A Textures folder if missing
		if (!new File(FOLDER + "Textures").exists()) new File(FOLDER + "Textures").mkdir();
		
		// Add missing textures to Textures folder
		for (String file : DEFAULT_TEXTURES) {
			File dest = new File(FOLDER + "Textures", file);
			if (dest.exists()) continue;
			try {FileUtils.copyURLToFile(HudFileUtils.class.getResource(ASSETS + "Textures/" + file), dest);}
			catch (IOException e) {e.printStackTrace();}
		}
	}
	
	public static ResourceLocation getTexture(String filename) {
		sanitize(FOLDER + filename);
		return ResourceLocation.fromNamespaceAndPath("hudder",
				String.valueOf(getCRC32Checksum(filename.trim().toLowerCase())));
	}
	private static long getCRC32Checksum(String str) {return getCRC32Checksum(str.getBytes());}
	private static long getCRC32Checksum(byte[] bytes) {
	    Checksum crc32 = new CRC32();
	    crc32.update(bytes, 0, bytes.length);
	    return crc32.getValue();
	}

	public static void reloadResources() throws IOException {
		reader.clearCache();
		loadResources(new File(FOLDER), "");
		for (var listener : reloadResourcesListeners) listener.run();
	}



	public static void loadResources(File folder, String prefix) throws IOException {
		for (File resource : folder.listFiles()) {
			String path = prefix + ("".equals(prefix)?"":"/") + resource.getName();
			if (resource.isDirectory()) {
				loadResources(resource, path);
				continue;
			}
			try {
				var image = ImageIO.read(resource);
				if (image!=null) {
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					ImageIO.write(image, "PNG", output);
					reader.loadImageToCache(new ByteArrayInputStream(output.toByteArray()),getTexture(path));
					continue;
				}
			} catch (IOException e) {e.printStackTrace();}
			reader.loadFileToCache(resource);
		}
	}



	public static boolean imageLoaded(ResourceLocation id) {
		return reader.savedImages.containsKey(id);
	}
}