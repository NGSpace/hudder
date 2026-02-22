package dev.ngspace.hudder.hudpacks;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.mojang.blaze3d.platform.NativeImage;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.HudPackCompiler;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.BooleanNGSMCConfigOption;
import net.minecraft.network.chat.Component;

public class HudPack {
	
	private HudPackConfig configYaml;
	private HudPackCompiler compiler;
	private BufferedTexture[] bufferedtextures;
	private Map<String, HudPackSettings> settings;
	
	public HudPackPoint[] hudpackpoints;
	public int format_version = 0;
	
	public HudPack(String filepath, HudPackCompiler compiler) throws IOException {
		this.compiler = compiler;
		File file = new File(filepath);
		try (EntryReaderConsumer reader = file.isDirectory() ? new EntryReaderConsumer.Directory(file) :
				new EntryReaderConsumer.Zip(file)) {
			processConfig(reader);
			bufferTextures(reader, configYaml.texturesOrEmpty());
			loadTextures();
			loadSettings(configYaml.settingsOrEmpty());
		}
	}

	private void processConfig(EntryReaderConsumer reader) throws IOException {
        try (var in = new InputStreamReader(reader.readEntry("pack.json"))) {
            configYaml = new Gson().fromJson(in, HudPackConfig.class);
        }
        format_version = configYaml.format_version();
		hudpackpoints = new HudPackPoint[configYaml.points().size()];
		for (int i = 0;i<hudpackpoints.length;i++) {
			var point = configYaml.points().get(i);
	        try (var in = reader.readEntry(point.path())) {
				String point_code = new String(in.readAllBytes());
				hudpackpoints[i] = new HudPackPoint(point, point_code, compiler);
	        }
		}
	}
	
	private void bufferTextures(EntryReaderConsumer reader, List<String> textures) throws IOException {
		this.bufferedtextures = new BufferedTexture[textures.size()];
		for (int i = 0;i<textures.size();i++) {
			var texture = textures.get(i);
	        try (var in = reader.readEntry(texture)) {
	        	bufferedtextures[i] = new BufferedTexture(texture, IOUtils.toByteArray(in));
	        }
		}
	}

	private void loadTextures() throws IOException {
		for (BufferedTexture texture : bufferedtextures) {
			HudFileUtils.loadImage(NativeImage.read(texture.img()), texture.path());
		}
	}

	private void loadSettings(Map<String, HudPackSettings> settings) {
		this.settings = settings;
	}

	public boolean hasSettings() {
		return !settings.isEmpty();
	}

	public Set<String> getSettingsKeys() {
		return settings.keySet();
	}

	public AbstractNGSMCConfigOption<? extends Object> buildSetting(String setting) {
		var v = settings.get(setting);
		return switch (v.type()) {
			case "boolean","bool": {
				yield BooleanNGSMCConfigOption.builder(((Boolean) getSettingValue(setting)),
						Component.literal(v.name()))
					.setDefaultValue((Boolean) v.default_value())
					.setSaveOperation(val->setSettingValue(setting, val))
					.build();
			}
			default:
				throw new RuntimeException("THERE IS NO SETTING CALLED \"" + setting + '"');
		};
	}

	public Object getSettingValue(String string) {
		return Hudder.config.getHudSettings("hudpacks", Hudder.config.mainfile())
				.getOrDefault(string, settings.get(string).default_value());
	}

	public void setSettingValue(String string, Object value) {
		Hudder.config.getHudSettings("hudpacks",  Hudder.config.mainfile()).put(string, value);
	}
}
