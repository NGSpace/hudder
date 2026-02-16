package dev.ngspace.hudder.hudpacks;

import java.util.ArrayList;
import java.util.List;

public record HudPackConfig(int pack_version, List<HudPackPointConfig> points, List<String> textures) {
	
	public HudPackConfig(int pack_version, List<HudPackPointConfig> points) {
		this(pack_version, points, new ArrayList<String>());
	}
	
}
