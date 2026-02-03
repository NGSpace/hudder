package io.github.ngspace.hudder.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public class CachedReader {
	
	protected static Minecraft mc = Minecraft.getInstance();
	
	public Reader reader = new FilesRABReader();
	

	HashMap<String, byte[]> savedFiles = new HashMap<String, byte[]>();
	HashMap<String, String> savedFilesStrings = new HashMap<String, String>();
	HashMap<Identifier, DynamicTexture> savedImages = new HashMap<Identifier, DynamicTexture>();
	
	

	public byte[] getCachedFile(String file) {return savedFiles.get(file);}
	public String getCachedFileAsString(String file) {
		String contents = savedFilesStrings.get(file);
		if (contents == null) {
			contents = new String(getCachedFile(file));
			savedFilesStrings.put(file, contents);
			return contents;
		}
		return contents;
	}
	
	
	
	public boolean loadFileToCache(File file) throws IOException {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			if (!file.createNewFile()) return false;
		}
		savedFiles.put(file.getAbsolutePath(),reader.readFile(file));
		return true;
	}
	
	
	
	public boolean loadImageToCache(InputStream inputStream, Identifier id) throws IOException {
		if (savedImages.containsKey(id)) {
			mc.getTextureManager().release(id);
			savedImages.get(id).close();
			savedImages.remove(id);
		}
		NativeImage img = NativeImage.read(inputStream);
		DynamicTexture tex = new DynamicTexture(id::getPath,img);
		mc.getTextureManager().register(id, tex);
		
		savedImages.put(id,tex);
		return true;
	}
	
	
	
	/**
	 * Clears Cache
	 */
	public void clearCache() {
		for (Entry<Identifier, DynamicTexture> v : savedImages.entrySet()) {
			mc.getTextureManager().release(v.getKey());
			v.getValue().close();
		}
		savedImages.clear();
		savedFiles.clear();
		savedFilesStrings.clear();
	}
	
	public static interface Reader {
		public byte[] readFile(File f) throws IOException;
	}
	
	/**
	 * @deprecated Very limited
	 */
	@Deprecated(since = "8.6.0", forRemoval = true)
	public static class ScannerReader implements Reader {
		
		@Deprecated
		public byte[] readFile(File file) throws IOException {
			Scanner reader = new Scanner(file);
			String res = "";
			while (reader.hasNextLine()) {
				res += reader.nextLine();
				res += '\n';
			}
			reader.close();
			if (res.isEmpty()) return res.getBytes();
			return res.substring(0, res.length()-1).getBytes();
		}
		
	}
	
	public static class FilesRABReader implements Reader {
		
		public byte[] readFile(File file) throws IOException {
			return Files.readAllBytes(file.toPath());
		}
		
	}
}
