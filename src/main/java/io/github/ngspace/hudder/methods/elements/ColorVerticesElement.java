package io.github.ngspace.hudder.methods.elements;

import io.github.ngspace.hudder.util.HudderRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexFormat;

public class ColorVerticesElement extends AUIElement {
	
	private static final long serialVersionUID = 5234887884582993410L;
	
	final float[] vertices;
	final int r;
	final int g;
	final int b;
	final int a;
	final VertexFormat.DrawMode mode;
	
	public ColorVerticesElement(float[] vertices, long argb, boolean triangles) {
        this.vertices = vertices;
        this.a = (int) ((argb >> 24) & 0xFF);
        this.r = (int) ((argb >> 16) & 0xFF);
        this.g = (int) ((argb >>  8) & 0xFF);
        this.b = (int) ((argb      ) & 0xFF);
        this.mode = triangles ? VertexFormat.DrawMode.TRIANGLE_STRIP : VertexFormat.DrawMode.QUADS;
	}
	@Override public void renderElement(DrawContext context, HudderRenderer renderer, RenderTickCounter delta) {
		renderer.renderColoredVertexArray(context,vertices,r,g,b,a, mode);
	}
	
}
