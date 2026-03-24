package dev.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.ngspace.hudder.Hudder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftClientInjections {
	
    @Redirect(method = "renderFrame", at = @At(value = "INVOKE",
    		target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntryList;isCurrentlyEnabled(Lnet/minecraft/resources/Identifier;)Z"))
    public boolean isCurrentlyEnabled(DebugScreenEntryList list, Identifier loc) {
    	if (Hudder.config.enabled()) return true;
    	return list.isCurrentlyEnabled(loc);
    }
}
  