package io.github.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public interface WorldRendererAccess {
//	@Accessor int getRegularEntityCount();
}