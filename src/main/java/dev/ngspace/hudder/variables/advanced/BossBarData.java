package dev.ngspace.hudder.variables.advanced;

import dev.ngspace.hudder.api.variableregistry.ComponentWrapper;
import dev.ngspace.hudder.mixin.ChatFormattingAccessor;
import net.minecraft.world.BossEvent;

public class BossBarData {

	public final String uuid;
	public final String color;
	public final String color_code;
	public final ComponentWrapper name;
	public final boolean darken_screen;
	public final boolean play_boss_music;
	public final boolean create_world_fog;
	public final float progress;

	public BossBarData(BossEvent event) {
		this.uuid = event.getId().toString();
		this.color = event.getColor().getName();
		this.color_code = String.valueOf(((ChatFormattingAccessor)((Object)event.getColor().getFormatting())).code());
		this.name = new ComponentWrapper(event.getName());
		this.progress = event.getProgress();
		this.darken_screen = event.shouldDarkenScreen();
		this.play_boss_music = event.shouldPlayBossMusic();
		this.create_world_fog = event.shouldCreateWorldFog();
	}

	@Override
	public String toString() {
		return "BossBarData [uuid=" + uuid + ", color=" + color + ", color_code=" + color_code + ", name=" + name
				+ ", darken_screen=" + darken_screen + ", play_boss_music=" + play_boss_music + ", create_world_fog="
				+ create_world_fog + ", progress=" + progress + "]";
	}
}
