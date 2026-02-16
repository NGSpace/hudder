package dev.ngspace.hudder.hudpacks;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.mojang.blaze3d.platform.NativeImage;

import dev.ngspace.hudder.compilers.HudPackCompiler;
import dev.ngspace.hudder.utils.HudFileUtils;

public class HudPackImpl {
	
	private HudPackConfig configYaml;
	private HudPackCompiler compiler;
	private BufferedTexture[] bufferedtextures;
	
	public HudPackPoint[] hudpackpoints;
	public Object pack_version = 0;
	
	public HudPackImpl(String filepath, HudPackCompiler compiler) {
		this.compiler = compiler;
		try (ZipFile zipFile = new ZipFile(filepath)) {
			processConfig(zipFile, zipFile.getEntry("pack.json"));
			bufferTextures(zipFile, configYaml.textures());
			loadTextures();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processConfig(ZipFile zipFile, ZipEntry entry) throws IOException {
        try (var in = new InputStreamReader(zipFile.getInputStream(entry))) {
            configYaml = new Gson().fromJson(in, HudPackConfig.class);
        }
		pack_version = configYaml.pack_version();
		hudpackpoints = new HudPackPoint[configYaml.points().size()];
		for (int i = 0;i<hudpackpoints.length;i++) {
			var point = configYaml.points().get(i);
	        try (var in = zipFile.getInputStream(zipFile.getEntry(point.path()))) {
				String point_code = new String(in.readAllBytes());
				hudpackpoints[i] = new HudPackPoint(point, point_code, compiler);
	        }
		}
	}
	
	private void bufferTextures(ZipFile zipFile, List<String> textures) throws IOException {
		this.bufferedtextures = new BufferedTexture[textures.size()];
		for (int i = 0;i<textures.size();i++) {
			var texture = textures.get(i);
	        try (var in = zipFile.getInputStream(zipFile.getEntry(texture))) {
	        	bufferedtextures[i] = new BufferedTexture(texture, IOUtils.toByteArray(in));
	        }
		}
	}

	private void loadTextures() throws IOException {
		for (BufferedTexture texture : bufferedtextures) {
			HudFileUtils.loadImage(NativeImage.read(texture.img()), texture.path());
		}
	}
}
