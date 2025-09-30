package io.github.ngspace.hudder.mixin;

import static io.github.ngspace.hudder.Hudder.config;
import static io.github.ngspace.hudder.data_management.Advanced.keysheld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;

@Environment(EnvType.CLIENT)
@Mixin(KeyboardHandler.class)
public class KeyboardInjections {
	@Inject(method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", at = @At("HEAD"))
	public void keyPress(long window, int action, KeyEvent keyEvent, CallbackInfo callbackInfo) {
		if (!config.enabled) return;
		var key = keyEvent.key();
		if (action==1) keysheld.put(key, key);
		if (action==0) keysheld.remove(key);
	}
}