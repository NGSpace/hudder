package dev.ngspace.hudder.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.NativeImage;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.utils.Compilers;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class HudFileUtils {private HudFileUtils() {}

	private static CachedReader reader = new CachedReader();
	private static List<ResourceReloadListener> reloadResourcesListeners = new ArrayList<ResourceReloadListener>();

	public static String FABRIC_CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().toString();
	public static String FOLDER = FABRIC_CONFIG_FOLDER + File.separator + "hudder" + File.separator;
    public static String ASSETS = "/assets/hudder/";
    public static String[] DEFAULT_HUDS = {"hand.hud", "armorside.hud", "hud.hud", "basic.hud",
    		"hud.js", "hotbar.js"};
    public static String[] DEFAULT_TEXTURES = {"pointer.png","selection.png"};
	
    
    
    /**
     * Read file to String
     * @param file - the file to read
     * @return The text in the file
     * @throws IOException 
     */
	public static String readFile(String file) throws IOException {
		return reader.getCachedFileAsString(sanitize(FOLDER + file));
	}
	
    
    
	public static byte[] readFileBytes(String file) throws IOException {
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



	public static String readFileUnsanitized(File file) throws IOException {
		return new String(reader.reader.readFile(file));
	}
	
	
	
	/**
	 * Triggers when clearFileCache() is called (usually when the user modifies a hud)
	 * @param listener - the Runnable to trigger
	 */
	public static void addReloadResourcesListener(ResourceReloadListener listener) {
		reloadResourcesListeners.add(listener);
	}
	public static void addReloadResourcesListenerFirst(ResourceReloadListener listener) {
		reloadResourcesListeners.add(0, listener);
	}
	
	
	
	/**
	 * Checks if a filename is dirty or not.
	 * @param f - The name of the file
	 * @return the filename provided
	 * @throws SecurityException - If the provided filename is "dirty"
	 * @throws IOException 
	 */
	public static String sanitize(String f) throws SecurityException, IOException {
		Path d = Paths.get(f).toAbsolutePath().normalize();
		Path folder = Paths.get(FOLDER).toAbsolutePath().normalize();
		if (!d.startsWith(folder))
			throw new FileNotFoundException(f + " (No such file or directory)");
		
		int j = 0;
		int k = 0;
		for (int i = 0;i<f.length();i++) {
			char c = f.charAt(i);
			if (c=='.') j++;
			else if (c=='/'||c=='\\') {
				if (j==2&&k==0) throw new FileNotFoundException(f + " (No such file or directory)");
				k = 0;
			} else {j = 0;k++;}
		}
		
		return f;
	}
	
	
	
	/**
	 * Sanitizes the provided relative path and then checks if it exists
	 * @param file - the path
	 * @return whether the file exists or not
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public static boolean exists(String file) throws SecurityException, IOException {
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
			try {
				FileUtils.copyURLToFile(HudFileUtils.class.getResource(ASSETS + "huds/" + file), dest);
			} catch (IOException e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				Hudder.log("Failed to generate default hud " + file);
			}
		}
		
		// Create A Textures folder if missing
		if (!new File(FOLDER + "Textures").exists()) new File(FOLDER + "Textures").mkdir();
		
		// Add missing textures to Textures folder
		for (String file : DEFAULT_TEXTURES) {
			File dest = new File(FOLDER + "Textures", file);
			if (dest.exists()) continue;
			try {
				FileUtils.copyURLToFile(HudFileUtils.class.getResource(ASSETS + "Textures/" + file), dest);
			} catch (IOException e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				Hudder.log("Failed to generate default texture " + file);
			}
		}
	}
	
	@Nullable public static Identifier getTexture(String filename) throws SecurityException {
		try {
			sanitize(FOLDER + filename);
		} catch (IOException e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			return null;
		}
		return Identifier.fromNamespaceAndPath("hudder", String.valueOf(HudderUtils.getCRC32Checksum(filename.trim().toLowerCase())));
	}

	public static void reloadResources() throws IOException {
		reader.clearCache();
		loadResources(new File(FOLDER), "");
		for (var comp : Compilers.compilers()) comp.resetState();
		for (var listener : reloadResourcesListeners) listener.run();
	}



	public static void loadResources(File folder, String prefix) throws IOException {
		for (File resource : folder.listFiles()) {
			String path = prefix + ("".equals(prefix)?"":"/") + resource.getName();
			if (resource.isDirectory()) {
				loadResources(resource, path);
				continue;
			}
			if (loadImage(resource, path))
				continue;
			reader.loadFileToCache(resource);
		}
	}



	public static boolean loadImage(File resource, String path) throws IOException {
		var image = ImageIO.read(resource);
		if (image!=null) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", output);
			reader.markImageForRegisteration(new ByteArrayInputStream(output.toByteArray()),getTexture(path));
		}
		return image!=null;
	}



	public static void loadImage(NativeImage img, String path) throws SecurityException {
		reader.markImageForRegisteration(img,getTexture(path));
	}



	public static boolean imageLoaded(Identifier id) {
		return reader.imageLoaded(id);
	}
	
	
	
	public static void loadMarkedResources() {
		// This is to ensure texture loading is always on the render thread
		reader.loadUnregisteredImagesToTextureManager();
	}
}