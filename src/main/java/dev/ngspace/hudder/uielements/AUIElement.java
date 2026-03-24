package dev.ngspace.hudder.uielements;

import dev.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class AUIElement {
	public abstract void renderElement(GuiGraphicsExtractor context, HudderRenderer renderer, DeltaTracker delta);
}
