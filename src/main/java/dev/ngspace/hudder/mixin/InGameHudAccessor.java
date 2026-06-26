package dev.ngspace.hudder.mixin;

import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.contextualbar.ContextualBar;

@Mixin(Hud.class)
public interface InGameHudAccessor {
    @Invoker("extractPlayerHealth") public void callRenderPlayerHealth(GuiGraphicsExtractor context);
    @Invoker("extractVehicleHealth") public void callRenderVehicleHealth(GuiGraphicsExtractor context);
    @Invoker("extractSelectedItemName") public void callRenderSelectedItemName(GuiGraphicsExtractor context);
    @Invoker("extractHotbarAndDecorations") public void callRenderHotbarAndDecorations(GuiGraphicsExtractor context, DeltaTracker timeDelta);

    @Invoker public Hud.ContextualInfo callNextContextualInfoState();
    @Accessor("contextualInfoBar") public Pair<Hud.ContextualInfo, ContextualBar> contextualInfoBar();
    @Accessor("contextualInfoBars") public Map<Hud.ContextualInfo, Supplier<ContextualBar>> contextualInfoBarRenderers();
}
