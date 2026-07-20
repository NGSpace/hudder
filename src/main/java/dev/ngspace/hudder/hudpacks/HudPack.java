package dev.ngspace.hudder.hudpacks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.mojang.blaze3d.platform.NativeImage;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.HudPackCompiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.BooleanNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.DoubleNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.DropdownNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.HexNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.StringNGSMCConfigOption;
import net.minecraft.network.chat.Component;

public class HudPack {
	
	public static final int MAXIMUM_SUPPORTED_FORMAT = 2;
	private HudPackConfig configYaml;
	private HudPackCompiler compiler;
	private BufferedTexture[] bufferedtextures;
	private Map<String, HudPackSettings> settings;
	
	public HudPackPoint[] hudpackpoints;
	public HudPackEngineManager engineManager;
	public int format_version = 0;
	public Map<String, byte[]> entries = new HashMap<String, byte[]>();
	
	public HudPack(String filepath, HudPackCompiler compiler) throws IOException, CompileException {
		this.compiler = compiler;
		this.engineManager = new HudPackEngineManager(this.compiler, this);
		File file = new File(filepath);
		try (EntryReaderConsumer reader = file.isDirectory() ? new EntryReaderConsumer.Directory(file) :
				new EntryReaderConsumer.Zip(file)) {
			for (String entry : reader.listEntries()) {
				System.out.println(entry);
				entries.put(entry, reader.readEntry(entry).readAllBytes());
			}
			System.out.println(entries.get("code/coords.js"));
			processConfig(reader);
			bufferTextures(reader, configYaml.texturesOrEmpty());
			loadTextures();
			loadSettings(configYaml.settingsOrEmpty());
		}
	}

	private void processConfig(EntryReaderConsumer reader) throws IOException, CompileException {
        try (InputStreamReader in = new InputStreamReader(reader.readEntry("pack.json"))) {
            configYaml = new Gson().fromJson(in, HudPackConfig.class);
        }
        format_version = configYaml.format_version();
        if (format_version>MAXIMUM_SUPPORTED_FORMAT&&!Hudder.config.disableHudpackVersionCheck())
        	throw new CompileException("Unsupported Hud pack format version: " + format_version, -1, -1);
		hudpackpoints = new HudPackPoint[configYaml.points().size()];
		for (int i = 0;i<hudpackpoints.length;i++) {
			HudPackPointConfig point = configYaml.points().get(i);
	        try (InputStream in = reader.readEntry(point.path())) {
				String point_code = new String(in.readAllBytes());
				hudpackpoints[i] = new HudPackPoint(point, engineManager.getOrCreateEngine(point.path(), point_code));
	        }
		}
	}
	
	private void bufferTextures(EntryReaderConsumer reader, List<String> textures) throws IOException {
		this.bufferedtextures = new BufferedTexture[textures.size()];
		for (int i = 0;i<textures.size();i++) {
			String texture = textures.get(i);
	        try (InputStream in = reader.readEntry(texture)) {
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
		HudPackSettings v = settings.get(setting);
		
		if (format_version>1&&"dropdown".equals(v.type())) {
			return DropdownNGSMCConfigOption.builder((String) getSettingValue(setting),
					Component.literal(v.name()),
					List.of(v.values()))
				.setDefaultValue((String) v.default_value())
				.setSaveOperation(val->setSettingValue(setting, val))
				.build();
		}
		
		return switch (v.type()) {
			case "boolean": {
				yield BooleanNGSMCConfigOption.builder(((Boolean) getSettingValue(setting)),
						Component.literal(v.name()))
					.setDefaultValue((Boolean) v.default_value())
					.setSaveOperation(val->setSettingValue(setting, val))
					.build();
			}
			case "string": {
				yield StringNGSMCConfigOption.builder(String.valueOf(getSettingValue(setting)),
						Component.literal(v.name()))
					.setDefaultValue(String.valueOf(v.default_value()))
					.setSaveOperation(val->setSettingValue(setting, val))
					.build();
			}
			case "number": {
				yield DoubleNGSMCConfigOption.builder(((Number) getSettingValue(setting)).doubleValue(),
						Component.literal(v.name()))
					.setDefaultValue(((Number) v.default_value()).doubleValue())
					.setSaveOperation(val->setSettingValue(setting, val))
					.build();
			}
			case "hex": {
				yield HexNGSMCConfigOption.builder(((Number) getSettingValue(setting)).intValue(),
						Component.literal(v.name()))
					.setDefaultValue(((Number) v.default_value()).intValue())
					.setSaveOperation(val->setSettingValue(setting, val))
					.build();
			}
			default:
				throw new IllegalArgumentException("No setting of type \"" + v.type() + '"');
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
