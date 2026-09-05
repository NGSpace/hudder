package dev.ngspace.hudder.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.NativeImage;

import dev.ngspace.hudder.Hudder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class HudFileUtils {private HudFileUtils() {}

	private static CachedReader reader = new CachedReader();
	private static List<ResourceReloadListener> reloadResourcesListeners = new ArrayList<ResourceReloadListener>();

	public static Path FABRIC_CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir();
	public static Path FOLDER = FABRIC_CONFIG_FOLDER.resolve("hudder");
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
	public static String readFile(Path path) throws IOException {
		return reader.getCachedFileAsString(sanitize(FOLDER.resolve(path)));
	}
	
    
    
	public static byte[] readFileBytes(Path path) throws IOException {
		return reader.getCachedFile(sanitize(FOLDER.resolve(path)));
	}
	
	
	
    /**
     * Read file to String
     * @param file - the file to read
     * @return The text in the file
     * @throws IOException
     */
	public static String readFileWithoutCache(String file) throws IOException {
		return new String(reader.reader.readFile(sanitize(FOLDER.resolve(file))));
	}



	public static String readFileUnsanitized(Path file) throws IOException {
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
	 */
	public static Path sanitize(Path f) throws SecurityException, IOException {
		Path d = f.toAbsolutePath().normalize();
		Path folder = FOLDER.toAbsolutePath().normalize();
		if (!d.startsWith(folder))
			throw new FileNotFoundException(f + " (No such file or directory)");
		
		int j = 0;
		int k = 0;
		for (int i = 0;i<f.toString().length();i++) {
			char c = f.toString().charAt(i);
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
		return Files.exists(sanitize(FOLDER.resolve(file)));
	}
	
	
	
	/**
	 * Creates any missing default huds and default textures
	 * @throws  
	 */
	public static void makeDefaultHud() {
		
	    try {
	    	Hudder.log("Creating config folder");
	        Files.createDirectories(FOLDER);
	    	Hudder.log("Done creating config folder");
	    } catch (IOException e) {
	        e.printStackTrace();
	        Hudder.error("Failed to create hudder folder");
	        return;
	    }

		// Add missing huds to Hudder config folder (Assume one exists)
		for (String file : DEFAULT_HUDS) {
			Path dest = FOLDER.resolve(file);
			if (Files.exists(dest)) continue;
			try {
				Files.copy(HudFileUtils.class.getResourceAsStream(ASSETS + "huds/" + file), dest);
			} catch (IOException e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				Hudder.error("Failed to generate default hud " + file);
			}
		}
		
		// Create A Textures folder if missing
		
		Path textures = FOLDER.resolve("Textures");
		
		try {
			if (!Files.exists(textures))
				Files.createDirectories(textures);
		} catch (IOException e) {
			e.printStackTrace();
			Hudder.error("Failed to generate textures");
			return;
		}
		
		// Add missing textures to Textures folder
		for (String file : DEFAULT_TEXTURES) {
			Path dest = textures.resolve(file);
			if (Files.exists(dest)) continue;
			try {
				Files.copy(HudFileUtils.class.getResourceAsStream(ASSETS + "Textures/" + file), dest);
			} catch (IOException e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				Hudder.error("Failed to generate default texture " + file);
			}
		}
	}
	
	@Nullable public static Identifier getTexture(String filename) throws SecurityException {
		try {
			sanitize(FOLDER.resolve(filename));
		} catch (IOException e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			return null;
		}
		return Identifier.fromNamespaceAndPath("hudder", String.valueOf(HudderUtils.getCRC32Checksum(filename.trim().toLowerCase())));
	}

	public static void reloadResources() throws IOException {
		reader.clearCache();
		loadResources(FOLDER, "");
		for (var listener : reloadResourcesListeners) listener.run();
	}



	public static void loadResources(Path folder, String prefix) throws IOException {
		for (Path resource : Files.newDirectoryStream(folder)) {
			String path = prefix + ("".equals(prefix)?"":"/") + resource.getFileName();
			if (Files.isDirectory(resource)) {
				loadResources(resource, path);
				continue;
			}
			if (loadImage(resource, path))
				continue;
			reader.loadFileToCache(resource);
		}
	}



	public static boolean loadImage(Path resource, String path) throws IOException {
		var image = ImageIO.read(Files.newInputStream(resource));
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