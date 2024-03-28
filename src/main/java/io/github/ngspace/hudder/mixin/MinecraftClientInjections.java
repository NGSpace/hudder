package io.github.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.data_management.DataStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientInjections {
    @Shadow private double gpuUtilizationPercentage;

    @Inject(method = "render", at = @At(value = "INVOKE",
    		target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    public void getGpuUsage(boolean tick, CallbackInfo ci) {
    	DataStorage.gpuUsage = gpuUtilizationPercentage > 100 ? 100 : gpuUtilizationPercentage;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE",
    		target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z"))
    public boolean shouldGetGpuUsage(DebugHud hud) {
        return hud.shouldShowDebugHud() || ConfigManager.getConfig().enabled;
    }
}
