package io.github.ngspace.hudder.util;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.ngspace.hudder.Hudder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

public class HudFileUtils {private HudFileUtils() {}
	
	public static List<Runnable> chacheClearRunnables = new ArrayList<Runnable>();

	public static final String FABRIC_CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().toString();
	public static final String FOLDER = FABRIC_CONFIG_FOLDER + separator + Hudder.MOD_ID + separator;
	
	private static CachedReader reader = new CachedReader(); static {addClearCacheListener(reader);}
	
	public static String getFile(String file) throws IOException {return reader.getFile(FOLDER + sanitize(file));}
	public static NativeImage getAndRegisterImage(String file, Identifier id) throws IOException {
		return reader.getAndRegisterImage(FOLDER + sanitize(file), id);
	}
	public static void clearCache() {for (Runnable r : chacheClearRunnables) r.run();}
	public static void addClearCacheListener(Runnable listener) {chacheClearRunnables.add(listener);}
	
	public static String sanitize(String f) {
		int j = 0;
		int k = 0;
		for (int i = 0;i<f.length();i++) {
			char c = f.charAt(i);
			if (c=='.') j++; else if (c=='/'||c=='\\') {
				if (j==2&&k==0) throw new FuckYouException("Fuck you, I ain't this stupid to accept this: "+f);k = 0;
			} else {j = 0;k++;}
		}
		return f;
	}
	public static boolean exists(String file) {
		return new File(file).exists();
	}
}