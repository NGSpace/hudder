package io.github.ngspace.hudder.mixin;

import static io.github.ngspace.hudder.Hudder.config;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftClientInjections {
	
    @Redirect(method = "runTick", at = @At(value = "INVOKE",
    		target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntryList;isCurrentlyEnabled(Lnet/minecraft/resources/ResourceLocation;)Z"))
    public boolean shouldGetGpuUsage(DebugScreenEntryList list, ResourceLocation loc) {
    	if (config.enabled) return true;
    	return list.isCurrentlyEnabled(loc);
    }
}
  