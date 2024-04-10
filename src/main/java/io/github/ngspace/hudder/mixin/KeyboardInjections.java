package io.github.ngspace.hudder.mixin;

import static io.github.ngspace.hudder.data_management.Advanced.keysheld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.config.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;

@Environment(EnvType.CLIENT)
@Mixin(Keyboard.class)
public class KeyboardInjections {
	ConfigInfo config = ConfigManager.getConfig();
	@Inject(method = "onKey(JIIII)V", at = @At("HEAD"))
	public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo callbackInfo) {
		if (!config.enabled) return;
		if (action==1) keysheld.put(key, key);
		if (action==0) keysheld.remove(key);
	}
}