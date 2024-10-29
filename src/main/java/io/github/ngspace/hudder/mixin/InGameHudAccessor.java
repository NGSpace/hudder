package io.github.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.JumpingMount;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {
    @Invoker public void callRenderStatusBars(DrawContext context);
    @Invoker public void callRenderMountHealth(DrawContext context);
    @Invoker public void callRenderMountJumpBar(JumpingMount mount, DrawContext context, int x);
    @Invoker public void callRenderExperienceBar(DrawContext context, int x);
    @Invoker public void callRenderExperienceLevel(DrawContext context, RenderTickCounter tickCounter);
    @Invoker public void callRenderHeldItemTooltip(DrawContext context);
    @Invoker public void callRenderHotbar(DrawContext context, RenderTickCounter timeDelta);
}
