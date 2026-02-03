package dev.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ngspace.hudder.data_management.Advanced;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerInjections {
    @Inject(method = "onButton", at = @At("HEAD"))
    public void checkCPS(long l, MouseButtonInfo mouseButtonInfo, int i, CallbackInfo ci) {
    	if (i==1) {
	        if (mouseButtonInfo.button() == 0)
	        	Advanced.clicks_left.add(System.currentTimeMillis());
	        if (mouseButtonInfo.button() == 1)
	            Advanced.cps_right.add(System.currentTimeMillis());
    	}
    }
	
}
