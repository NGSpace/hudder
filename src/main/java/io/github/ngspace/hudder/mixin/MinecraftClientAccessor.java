package io.github.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlTimer;
import net.minecraft.util.profiler.Recorder;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
	@Accessor("recorder") public Recorder recorder();
	@Accessor("currentGlTimerQuery") public GlTimer.Query currentGlTimerQuery();
}
