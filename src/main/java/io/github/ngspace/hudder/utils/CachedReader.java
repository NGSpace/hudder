package io.github.ngspace.hudder.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

public class CachedReader {
	
	protected static Minecraft mc = Minecraft.getInstance();
	
	
	HashMap<String, String> savedFiles = new HashMap<String, String>();
	HashMap<ResourceLocation, Image> savedImages = new HashMap<ResourceLocation, Image>();
	
	
	
	public String readFile(String file) {return savedFiles.get(file);}
	
	
	
	public boolean readFileAndSaveToCache(File f) throws IOException {
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			if (!f.createNewFile()) return false;
		}
		savedFiles.put(f.getAbsolutePath(),readFileLineByLine(f));
		return true;
	}
	
	public String readFileLineByLine(File f) throws IOException {
		Scanner reader = new Scanner(f);
		String res = "";
		while (reader.hasNextLine()) {
			res += reader.nextLine();
			res += '\n';
		}
		reader.close();
		if (res.isEmpty()) return res;
		return res.substring(0, res.length()-1);
	}
	
	
	
	public boolean registerAndCacheImage(InputStream inputStream, ResourceLocation id, String path) throws IOException {
		if (savedImages.containsKey(id)) {
			mc.getTextureManager().release(id);
			savedImages.get(id).texture().close();
			savedImages.remove(id);
		}
		NativeImage img = NativeImage.read(inputStream);
		DynamicTexture tex = new DynamicTexture(img);
		mc.getTextureManager().register(id, tex);
		
		tex.bind();
		
		savedImages.put(id,new Image(tex, path));
		return true;
	}
	
	record Image(DynamicTexture texture, String path) {
	}
	
	
	
	/**
	 * Clears Cache
	 */
	public void clearCache() {
		for (Entry<ResourceLocation, Image> v : savedImages.entrySet()) {
			mc.getTextureManager().release(v.getKey());
			v.getValue().texture().close();
		}
		savedImages.clear();
		savedFiles.clear();
	}



	public String getImageName(ResourceLocation id) {
		return savedImages.get(id).path();
	}
}
