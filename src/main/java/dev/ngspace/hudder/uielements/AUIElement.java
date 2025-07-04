package dev.ngspace.hudder.uielements;

import dev.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public abstract class AUIElement {
	public abstract void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta);
}
