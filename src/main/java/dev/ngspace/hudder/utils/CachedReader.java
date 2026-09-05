package dev.ngspace.hudder.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map.Entry;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public class CachedReader {
	
	protected static Minecraft mc = Minecraft.getInstance();
	
	public Reader reader = new FilesRABReader();
	

	HashMap<Path, byte[]> savedFiles = new HashMap<>();
	HashMap<Path, String> savedFilesStrings = new HashMap<>();
	HashMap<Identifier, DynamicTexture> savedImages = new HashMap<>();
	HashMap<Identifier, NativeImage> unregisteredImages = new HashMap<>();
	
	

	public byte[] getCachedFile(Path file) {return savedFiles.get(file);}
	public String getCachedFileAsString(Path file) {
		String contents = savedFilesStrings.get(file);
		if (contents == null) {
			contents = new String(getCachedFile(file));
			savedFilesStrings.put(file, contents);
			return contents;
		}
		return contents;
	}
	
	
	
	public boolean loadFileToCache(Path file) throws IOException {
		if (!Files.exists(file)) {
			Files.createDirectories(file.getParent());
			Files.createFile(file);
		}
		savedFiles.put(file.toAbsolutePath(),reader.readFile(file));
		return true;
	}
	
	
	
	public void markImageForRegisteration(InputStream inputStream, Identifier id) throws IOException {
		markImageForRegisteration(NativeImage.read(inputStream), id);
	}
	
	
	
	public void markImageForRegisteration(NativeImage img, Identifier id) {
		unregisteredImages.put(id, img);
	}

	public void loadUnregisteredImagesToTextureManager() {
		if (unregisteredImages.isEmpty())
			return;
		for (var entry : unregisteredImages.entrySet())
			loadImageToTextureManager(entry.getValue(), entry.getKey());
		unregisteredImages.clear();
	}

	public void loadImageToTextureManager(NativeImage img, Identifier id) {
		if (savedImages.containsKey(id)) {
			mc.getTextureManager().release(id);
			savedImages.get(id).close();
			savedImages.remove(id);
		}
		DynamicTexture tex = new DynamicTexture(id::getPath,img);
		mc.getTextureManager().register(id, tex);
		
		savedImages.put(id,tex);
	}
	
	
	
	/**
	 * Clears Cache
	 */
	public void clearCache() {
		for (Entry<Identifier, DynamicTexture> v : savedImages.entrySet()) {
			mc.getTextureManager().release(v.getKey());
			v.getValue().close();
		}
		for (Entry<Identifier, NativeImage> v : unregisteredImages.entrySet()) {
			mc.getTextureManager().release(v.getKey());
			v.getValue().close();
		}
		unregisteredImages.clear();
		savedImages.clear();
		savedFiles.clear();
		savedFilesStrings.clear();
	}
	
	public static interface Reader {
		public byte[] readFile(Path f) throws IOException;
	}
	
	public static class FilesRABReader implements Reader {
		public byte[] readFile(Path file) throws IOException {
			return Files.readAllBytes(file);
		}
	}

	public boolean imageLoaded(Identifier id) {
		return savedImages.containsKey(id) || unregisteredImages.containsKey(id);
	}
}
