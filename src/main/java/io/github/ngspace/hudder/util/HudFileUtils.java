package io.github.ngspace.hudder.util;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

public class HudFileUtils {private HudFileUtils() {}

	private static CachedReader reader = new CachedReader();
	private static List<ERunnable<CompileException>> chacheClearRunnables = new ArrayList<ERunnable<CompileException>>();
	
	static {addClearFileCacheListener(reader);}

	public static final String FABRIC_CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().toString();
	public static final String FOLDER = FABRIC_CONFIG_FOLDER + separator + "hudder" + separator;
    public static final String ASSETS = "/assets/hudder/";
	
    
    
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
	public static NativeImage getAndRegisterImage(String file, Identifier id) throws IOException {
		return reader.getAndRegisterImage(sanitize(FOLDER + file), id);
	}
	
	
	
	public static void clearFileCache() throws CompileException {
		for(ERunnable<CompileException> r : chacheClearRunnables) r.run();
	}
	public static void addClearFileCacheListener(ERunnable<CompileException> listener) {
		chacheClearRunnables.add(listener);
	}
	
	
	
	public static String sanitize(String file) {
		if (!new File(file).getAbsolutePath().startsWith(FOLDER)) throwError(file);
		int j = 0;
		int k = 0;
		for (int i = 0;i<file.length();i++) {
			char c = file.charAt(i);
			if (c=='.') j++;
			else if (c=='/'||c=='\\') {
				if (j==2&&k==0) throwError(file);
				k = 0;
			} else {j = 0;k++;}
		}
		return file;
	}
	
	private static final void throwError(String file) {
		throw new SecurityException(file + " (No such file or directory)");
	}
	
	
	
	public static boolean exists(String file) {
		return new File(sanitize(FOLDER + file)).exists();
	}
	
	
	
	public static void makeDefaultConfig() {
		String[] defaultfiles = {"tutorial","hand","armor","armorside","hud","basic","hud.js","hotbar.js", "fibonacci"};
		
		for (String file : defaultfiles) {
			File dest = new File(FOLDER, file);
			if (dest.exists()) continue;
			try {FileUtils.copyURLToFile(HudFileUtils.class.getResource(ASSETS + file), dest);}
			catch (IOException e) {e.printStackTrace();}
		}
		
		if (!new File(FOLDER + "Textures").exists()) new File(FOLDER + "Textures").mkdir();
		
		String[] defaulttextures = {"pointer.png","selection.png"};
		
		for (String file : defaulttextures) {
			File dest = new File(FOLDER + "Textures", file);
			if (dest.exists()) continue;
			try {FileUtils.copyURLToFile(HudFileUtils.class.getResource(ASSETS+"Textures/" + file), dest);}
			catch (IOException e) {e.printStackTrace();}
		}
	}
}