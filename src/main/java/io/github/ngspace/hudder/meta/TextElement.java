package io.github.ngspace.hudder.meta;

import static io.github.ngspace.hudder.Hudder.renderText;

import net.minecraft.client.gui.DrawContext;

public class TextElement extends Element {
	
	private static final long serialVersionUID = -7265671672610205526L;
	
	public final String text;
	public final int x;
	public final int y;
	public final int color;
	public final float scale;
	public final boolean shadow;

	public TextElement(int x, int y, String text, float scale, int color, boolean shadow) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.text = text;
		this.color = color;
		this.shadow = shadow;
	}
	@Override public void RenderElement(DrawContext context) {
		renderText(context, text, x, y, color, scale, shadow);
	}
}