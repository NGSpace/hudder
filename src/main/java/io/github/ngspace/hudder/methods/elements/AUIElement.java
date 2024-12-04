package io.github.ngspace.hudder.methods.elements;

import java.io.Serializable;

import io.github.ngspace.hudder.hudder.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public abstract class AUIElement implements Serializable {
	private static final long serialVersionUID = 6926629886320975623L;
	public abstract void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta);
}
