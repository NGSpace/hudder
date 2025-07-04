package dev.ngspace.hudder.mixin;

import static dev.ngspace.hudder.Hudder.config;
import static dev.ngspace.hudder.data_management.Advanced.keysheld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyboardHandler;

@Environment(EnvType.CLIENT)
@Mixin(KeyboardHandler.class)
public class KeyboardInjections {
	@Inject(method = "keyPress(JIIII)V", at = @At("HEAD"))
	public void keyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo callbackInfo) {
		if (!config.enabled) return;
		if (action==1) keysheld.put(key, key);
		if (action==0) keysheld.remove(key);
	}
}