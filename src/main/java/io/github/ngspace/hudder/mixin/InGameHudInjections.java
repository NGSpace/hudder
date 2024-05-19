package io.github.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.ngspace.hudder.Hudder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudInjections {
	public boolean shouldNotDraw() {return Hudder.config.removegui&&Hudder.config.shouldCompile(Hudder.ins);}

	@Inject(method = "renderMainHud", at = @At("HEAD"),cancellable=true)
    public void disableMainHud(DrawContext context, float x, CallbackInfo i) {if (shouldNotDraw()) i.cancel();}
	@Inject(method = "renderExperienceLevel", at = @At("HEAD"),cancellable=true)
    public void disableExperienceLevel(DrawContext context, float x, CallbackInfo i) {if (shouldNotDraw()) i.cancel();}
}