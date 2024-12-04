package io.github.ngspace.hudder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.PlayerRideableJumping;

@Mixin(Gui.class)
public interface InGameHudAccessor {
    @Invoker public void callRenderPlayerHealth(GuiGraphics context);
    @Invoker public void callRenderVehicleHealth(GuiGraphics context);
    @Invoker public void callRenderJumpMeter(PlayerRideableJumping mount, GuiGraphics context, int x);
    @Invoker public void callRenderExperienceBar(GuiGraphics guiGraphics, int i);
    @Invoker public void callRenderSelectedItemName(GuiGraphics context);
    @Invoker public void callRenderHotbarAndDecorations(GuiGraphics context, DeltaTracker timeDelta);
}
