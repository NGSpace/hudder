package dev.ngspace.hudder.mixin;

import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Gui.ContextualInfo;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;

@Mixin(Gui.class)
public interface InGameHudAccessor {
    @Invoker("extractPlayerHealth") public void callRenderPlayerHealth(GuiGraphicsExtractor context);
    @Invoker("extractVehicleHealth") public void callRenderVehicleHealth(GuiGraphicsExtractor context);
    @Invoker("extractSelectedItemName") public void callRenderSelectedItemName(GuiGraphicsExtractor context);
    @Invoker("extractHotbarAndDecorations") public void callRenderHotbarAndDecorations(GuiGraphicsExtractor context, DeltaTracker timeDelta);

    @Invoker public Gui.ContextualInfo callNextContextualInfoState();
    @Accessor("contextualInfoBar") public Pair<ContextualInfo, ContextualBarRenderer> contextualInfoBar();
    @Accessor("contextualInfoBarRenderers") public Map<ContextualInfo, Supplier<ContextualBarRenderer>> contextualInfoBarRenderers();
}
