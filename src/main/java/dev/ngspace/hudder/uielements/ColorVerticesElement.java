package dev.ngspace.hudder.uielements;

import dev.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ColorVerticesElement extends AUIElement {
	
	final float[] vertices;
	final int argb;
	final boolean mode;
	
	public ColorVerticesElement(float[] vertices, int argb, boolean triangle_strip) {
        this.vertices = vertices;
        this.argb = argb;
        this.mode = triangle_strip;
	}
	@Override public void renderElement(GuiGraphicsExtractor context, HudderRenderer renderer, DeltaTracker delta) {
		renderer.renderColoredVertexArray(context, vertices, argb, mode);
	}
	
}
