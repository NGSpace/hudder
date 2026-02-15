package dev.ngspace.hudder.hudpacks;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.Gson;

import dev.ngspace.hudder.compilers.HudPackCompiler;

public class HudPackImpl implements HudPack {
	
	Gson gson = new Gson();
	HudPackConfig configYaml;
	public HudPackPoint[] hudpackpoints;
	public Object pack_version = 0;
	private HudPackCompiler compiler;
	
	public HudPackImpl(String filepath, HudPackCompiler compiler) {
		this.compiler = compiler;
		try (ZipFile zipFile = new ZipFile(filepath)) {
			processConfig(zipFile, zipFile.getEntry("pack.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processConfig(ZipFile zipFile, ZipEntry entry) throws IOException {
        try (var in = new InputStreamReader(zipFile.getInputStream(entry))) {
            configYaml = gson.fromJson(in, HudPackConfig.class);
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

	@Override
	public void close() throws Exception {/* */}
	
}
