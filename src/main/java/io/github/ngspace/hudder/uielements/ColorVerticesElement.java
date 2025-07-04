package io.github.ngspace.hudder.uielements;

import io.github.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public class ColorVerticesElement extends AUIElement {
	
	final float[] vertices;
	final int argb;
	final boolean mode;
	
	public ColorVerticesElement(float[] vertices, int argb, boolean triangle_strip) {
        this.vertices = vertices;
        this.argb = argb;
        this.mode = triangle_strip;
	}
	@Override public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {
		renderer.renderColoredVertexArray(context,vertices,argb, mode);
	}
	
}
