package dev.ngspace.hudder.hudpacks;

import java.util.List;
import java.util.Map;

public record HudPackConfig(int format_version, List<HudPackPointConfig> points, List<String> textures,
		Map<String, HudPackSettings> settings) {
    public List<String> texturesOrEmpty() {
        return textures == null ? List.of() : textures;
    }

    public Map<String, HudPackSettings> settingsOrEmpty() {
        return settings == null ? Map.of() : settings;
    }
}