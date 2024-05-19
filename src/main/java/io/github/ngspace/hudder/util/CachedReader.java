package io.github.ngspace.hudder.util;

import static io.github.ngspace.hudder.Hudder.ins;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;

import io.github.ngspace.hudder.compilers.CompileException;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

/**
 * I decided to hide this class in this package for safety.
 */
class CachedReader implements ERunnable<CompileException> {
	HashMap<String, String> savedFiles = new HashMap<String, String>();
	HashMap<Identifier, Images> savedImages = new HashMap<Identifier, Images>();
	class Images implements Closeable {
		final NativeImage img;
		final NativeImageBackedTexture tex;
		public Images(NativeImageBackedTexture tex, NativeImage img) {this.img = img;this.tex = tex;}
		@Override public void close() {tex.close();img.close();ins.getTextureManager();}
	}
	
	public String getFile(String file) throws IOException {
		if (!savedFiles.containsKey(file)) readFile(file);
		return savedFiles.get(file);
	}
	
	private boolean readFile(String file) throws IOException {
		File f = new File(file);
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			if (!f.createNewFile()) return false;
		}
		savedFiles.put(file,new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8));
		return true;
	}
	@SuppressWarnings("resource")
	public NativeImage getAndRegisterImage(String file, Identifier id) throws IOException {
		if (!savedImages.containsKey(id)) readAndRegisterImage(file, id);
		return savedImages.get(id).img;
	}
	private boolean readAndRegisterImage(String file, Identifier id) throws IOException {
		if (savedImages.containsKey(id)) {
			savedImages.get(id).close();
			savedImages.remove(id);
		}
		NativeImage img = NativeImage.read(new FileInputStream(new File(file)));
		NativeImageBackedTexture tex = new NativeImageBackedTexture(img);
		tex.registerTexture(ins.getTextureManager(), ins.getResourceManager(), id, null);
		
		tex.bindTexture();
		
		savedImages.put(id,new Images(tex,img));
		return true;
	}
	public String sanitize(String file) {
		int j = 0;
		int k = 0;
		for (int i = 0;i<file.length();i++) {
			char c = file.charAt(i);
			if (c=='.') j++;
			else if (c=='/'||c=='\\') {
				if (j==2&&k==0) throw new FuckYouException("Fuck you, I ain't this stupid to accept this: " + file);
				k = 0;
			} else {j = 0;k++;}
		}
		return file;
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
