package io.github.ngspace.hudder.uielements;

import io.github.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public class ColorVerticesElement extends AUIElement {
	
	final float[] vertices;
	final int r;
	final int g;
	final int b;
	final int a;
	final boolean mode;
	
	public ColorVerticesElement(float[] vertices, long argb, boolean triangle_strip) {
        this.vertices = vertices;
        this.a = (int) ((argb >> 24) & 0xFF);
        this.r = (int) ((argb >> 16) & 0xFF);
        this.g = (int) ((argb >>  8) & 0xFF);
        this.b = (int) ((argb      ) & 0xFF);
        this.mode = triangle_strip;
	}
	@Override public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {
		renderer.renderColoredVertexArray(context,vertices,r,g,b,a, mode);
	}
	
}
