package io.github.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.data_management.Advanced;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientInjections {
    @Shadow private double gpuUtilizationPercentage;

//    @Inject(method = "render", at = @At(value = "INVOKE",
//    		target = "Ljava/lang/String;format(Ljava/util/L"
//    				+ "ocale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
//    public void getGpuUsage(boolean tick, CallbackInfo ci) {
//    	Advanced.gpuUsage = gpuUtilizationPercentage > 100 ? 100 : gpuUtilizationPercentage;
//    }
//    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z"))
//    public boolean shouldGetGpuUsage(GameOptions hud){return Hudder.ins.options.hudHidden||ConfigManager.getConfig().enabled;}
}