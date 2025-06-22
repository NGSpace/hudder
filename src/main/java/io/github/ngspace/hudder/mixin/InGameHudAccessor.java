package io.github.ngspace.hudder.mixin;

import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui.ContextualInfo;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public interface InGameHudAccessor {
    @Invoker public void callRenderPlayerHealth(GuiGraphics context);
    @Invoker public void callRenderVehicleHealth(GuiGraphics context);
    @Invoker public void callRenderSelectedItemName(GuiGraphics context);
    @Invoker public void callRenderHotbarAndDecorations(GuiGraphics context, DeltaTracker timeDelta);

    @Shadow public Gui.ContextualInfo nextContextualInfoState();
    @Accessor("contextualInfoBar") public Pair<ContextualInfo, ContextualBarRenderer> contextualInfoBar();
    @Accessor("contextualInfoBarRenderers") public Map<ContextualInfo, Supplier<ContextualBarRenderer>> contextualInfoBarRenderers();
}
