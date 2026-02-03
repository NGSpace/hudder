package dev.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ngspace.hudder.Hudder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public class InGameHudInjections {
	public boolean shouldNotDraw() {return Hudder.config.removegui&&Hudder.config.shouldCompile();}

	@Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"),cancellable=true)
    public void disableHotbarAndDecorations(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo i) {
		if(shouldNotDraw()) {
			i.cancel();
		}
	}
}