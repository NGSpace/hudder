package io.github.ngspace.hudder.uielements;

import com.mojang.blaze3d.vertex.VertexFormat;

import io.github.ngspace.hudder.hudder.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public class ColorVerticesElement extends AUIElement {
	
	private static final long serialVersionUID = 5234887884582993410L;
	
	final float[] vertices;
	final int r;
	final int g;
	final int b;
	final int a;
	final VertexFormat.Mode mode;
	
	public ColorVerticesElement(float[] vertices, long argb, boolean triangles) {
        this.vertices = vertices;
        this.a = (int) ((argb >> 24) & 0xFF);
        this.r = (int) ((argb >> 16) & 0xFF);
        this.g = (int) ((argb >>  8) & 0xFF);
        this.b = (int) ((argb      ) & 0xFF);
        this.mode = triangles ? VertexFormat.Mode.TRIANGLE_STRIP : VertexFormat.Mode.QUADS;
	}
	@Override public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {
		renderer.renderColoredVertexArray(context,vertices,r,g,b,a, mode);
	}
	
}
