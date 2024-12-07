package io.github.ngspace.hudder.uielements;

import io.github.ngspace.hudder.hudder.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public abstract class AUIElement {
	public abstract void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta);
}
