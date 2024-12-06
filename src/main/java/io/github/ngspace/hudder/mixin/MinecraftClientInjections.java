package io.github.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.data_management.Advanced;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftClientInjections {
	
    @Shadow private double gpuUtilization;
    @Inject(method = "runTick", at = @At(value = "INVOKE",
    		target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    public void getGpuUsage(boolean tick, CallbackInfo ci) {
    	Advanced.gpuUsage = gpuUtilization > 100 ? 100 : gpuUtilization;
    }
    
    @Redirect(method = "runTick", at = @At(value = "INVOKE",
    		target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;showDebugScreen()Z"))
    public boolean shouldGetGpuUsage(DebugScreenOverlay hud) {
    	return hud.showDebugScreen()||ConfigManager.getConfig().enabled;
    }
}
  