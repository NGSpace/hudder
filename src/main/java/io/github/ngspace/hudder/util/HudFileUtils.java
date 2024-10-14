package io.github.ngspace.hudder.util;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

public class HudFileUtils {private HudFileUtils() {}
	
	public static List<ERunnable<CompileException>> chacheClearRunnables = new ArrayList<ERunnable<CompileException>>();

	public static final String FABRIC_CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().toString();
	public static final String FOLDER = FABRIC_CONFIG_FOLDER + separator + Hudder.MOD_ID + separator;
	
	private static CachedReader reader = new CachedReader(); static {addClearCacheListener(reader);}
	
	public static String getFile(String file) throws IOException {return reader.getFile(FOLDER + sanitize(file));}
	public static NativeImage getAndRegisterImage(String file, Identifier id) throws IOException {
		return reader.getAndRegisterImage(FOLDER + sanitize(file), id);
	}
	public static void clearCache()throws CompileException{for(ERunnable<CompileException>r:chacheClearRunnables)r.run();}
	public static void addClearCacheListener(ERunnable<CompileException> listener) {chacheClearRunnables.add(listener);}
	
	public static String sanitize(String f) {
		int j = 0;
		int k = 0;
		for (int i = 0;i<f.length();i++) {
			char c = f.charAt(i);
			if (c=='.') j++; else if (c=='/'||c=='\\') {
				if (j==2&&k==0) {throw new UnauthorizedFileIOException("Attempting to access protected path: "+f);}k = 0;
			} else {j = 0;k++;}
		}
		return f;
	}
	public static boolean exists(String file) {
		return new File(sanitize(file)).exists();
	}
}