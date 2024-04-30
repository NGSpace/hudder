package io.github.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {
    @Invoker void callRenderStatusBars(DrawContext context);
    @Invoker void callRenderMountHealth(DrawContext context);
    @Invoker void callRenderHotbar(float timeDelta, DrawContext context);
}
