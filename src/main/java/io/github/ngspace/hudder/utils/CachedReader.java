package io.github.ngspace.hudder.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import com.mojang.blaze3d.platform.NativeImage;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * I decided to hide this class in this package for safety.
 */
class CachedReader implements ERunnable<CompileException> {
	
	protected static Minecraft mc = Minecraft.getInstance();
	
	
	HashMap<String, String> savedFiles = new HashMap<String, String>();
	HashMap<ResourceLocation, Images> savedImages = new HashMap<ResourceLocation, Images>();
	
	
	
	class Images implements Closeable {
		final NativeImage img;
		final DynamicTexture tex;
		public Images(DynamicTexture tex, NativeImage img) {this.img = img;this.tex = tex;}
		@Override public void close() {tex.close();img.close();mc.getTextureManager();}
	}
	
	
	
	public String getFile(String file) throws IOException {
		if (!savedFiles.containsKey(file)) readFileAndSaveToCache(file);
		return savedFiles.get(file);
	}
	
	
	
	private boolean readFileAndSaveToCache(String file) throws IOException {
		File f = new File(HudFileUtils.sanitize(file));
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			if (!f.createNewFile()) return false;
		}
		savedFiles.put(file,readFileLineByLine(f));
		return true;
	}
	
	private String readFileLineByLine(File f) throws IOException {
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
	
	
	
	@SuppressWarnings("resource")
	public NativeImage getAndRegisterImage(String file, ResourceLocation id) throws IOException {
		if (!savedImages.containsKey(id)) readAndRegisterImage(file, id);
		return savedImages.get(id).img;
	}
	
	
	
	private boolean readAndRegisterImage(String file, ResourceLocation id) throws IOException {
		if (savedImages.containsKey(id)) {
			savedImages.get(id).close();
			savedImages.remove(id);
		}
		NativeImage img = NativeImage.read(new FileInputStream(new File(HudFileUtils.sanitize(file))));
		DynamicTexture tex = new DynamicTexture(img);
		mc.getTextureManager().register(id, tex);
		
		tex.bind();
		
		savedImages.put(id,new Images(tex,img));
		return true;
	}
	
	
	
	/**
	 * Clears Cache
	 */
	@Override public void run() {
		for (Images v : savedImages.values()) v.close();
		savedImages.clear();
		savedFiles.clear();
	}
}
