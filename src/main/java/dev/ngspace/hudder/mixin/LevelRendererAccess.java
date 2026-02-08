package dev.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.LevelRenderState;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public interface LevelRendererAccess {
	@Accessor LevelRenderState getLevelRenderState();
}