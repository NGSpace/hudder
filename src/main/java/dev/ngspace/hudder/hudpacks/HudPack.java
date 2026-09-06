package dev.ngspace.hudder.hudpacks;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.mojang.blaze3d.platform.NativeImage;

import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.defaultcompilers.HudPackCompiler;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.utils.HudderUtils;
import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.BooleanNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.DoubleNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.DropdownNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.HexNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.IntNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.StringNGSMCConfigOption;
import net.minecraft.network.chat.Component;

public class HudPack implements Closeable {

	public static final int MAXIMUM_SUPPORTED_FORMAT = 3;
	public static final int MAXIMUM_ENTRY_COUNT = 255;
	public static final int MAXIMUM_ENTRY_SIZE = 8388608; // 8 MiB
	public static final int MAXIMUM_PACK_SIZE = 67108864; // 64 MiB
	private HudPackConfig configYaml;
	private HudPackCompiler compiler;
	private BufferedTexture[] bufferedtextures;
	private Map<String, HudPackSettings> settings;
	private HudderConfig config;
	
	public HudPackPoint[] hudpackpoints;
	public HudPackEngineManager engineManager;
	public int format_version = 0;
	public Map<String, byte[]> entries = new HashMap<String, byte[]>();
	
	public HudPack(HudderConfig config, Path path, HudPackCompiler compiler) throws IOException, CompileException {
		this.compiler = compiler;
		this.engineManager = new HudPackEngineManager(this.compiler, this);
		this.config = config;
		try (EntryReaderConsumer reader = Files.isDirectory(path) ? new EntryReaderConsumer.Directory(path) :
				new EntryReaderConsumer.Zip(path)) {
			int entries_count = 0;
			int bytes_left = MAXIMUM_PACK_SIZE;
			for (String entry : reader.listEntries()) {
				if (entries_count==MAXIMUM_ENTRY_COUNT
						&&!config.unsafeoperations())
					throw new CompileException("Reached maximum entry count for Hudpacks!", -1, -1);
				entries_count++;
				InputStream input = reader.readEntry(entry);
				byte[] bytes = config.unsafeoperations() ?
						input.readAllBytes() :
						HudderUtils.limitedReadAllByte(input, Math.min(MAXIMUM_ENTRY_SIZE,
								bytes_left));
				input.close();
				bytes_left -= bytes.length;
				entries.put(entry, bytes);
			}
		}
		processConfig(config);
		bufferTextures(configYaml.texturesOrEmpty());
		loadTextures();
		loadSettings(configYaml.settingsOrEmpty());
	}

	private void processConfig(HudderConfig config) throws CompileException {
		// Check if pack.json exists
		if (!entries.containsKey("pack.json"))
			throw new CompileException("Missing entry: pack.json", -1, -1);
		// Read pack.json
        configYaml = new Gson().fromJson(new String(entries.get("pack.json"), StandardCharsets.UTF_8),
        		HudPackConfig.class);
        format_version = configYaml.format_version();
        // Check if format version is supported
        if (format_version>MAXIMUM_SUPPORTED_FORMAT&&!config.disableHudpackVersionCheck())
        	throw new CompileException("Unsupported Hud pack format version: " + format_version, -1, -1);
        // Read pack points
		hudpackpoints = new HudPackPoint[configYaml.points().size()];
		for (int i = 0;i<hudpackpoints.length;i++) {
			HudPackPointConfig point = configYaml.points().get(i);
			// Validate point actually exists
			if (!entries.containsKey(point.path()))
				throw new CompileException("Missing entry: " + point.path(), -1, -1);
			// Read point
			String point_code = new String(entries.get(point.path()), StandardCharsets.UTF_8);
			hudpackpoints[i] = new HudPackPoint(point, engineManager.getOrCreateEngine(point.path(), config, point_code));
		}
	}
	
	private void bufferTextures(List<String> textures) throws CompileException {
		this.bufferedtextures = new BufferedTexture[textures.size()];
		for (int i = 0;i<textures.size();i++) {
			String texture = textures.get(i);
			if (!entries.containsKey(texture))
				throw new CompileException("Missing Texture: " + texture, -1, -1);
        	bufferedtextures[i] = new BufferedTexture(texture, entries.get(texture));
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
		if (format_version>2&&"integer".equals(v.type())) {
			return IntNGSMCConfigOption.builder(((Number) getSettingValue(setting)).intValue(),
					Component.literal(v.name()))
				.setDefaultValue(((Number) v.default_value()).intValue())
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
		return config.getHudSettings("hudpacks", config.mainfileString()).getOrDefault(string,
				settings.get(string).default_value());
	}

	public void setSettingValue(String string, Object value) {
		config.getHudSettings("hudpacks",  config.mainfileString()).put(string, value);
	}

	@Override
	public void close() throws IOException {
		engineManager.close();
	}
}
