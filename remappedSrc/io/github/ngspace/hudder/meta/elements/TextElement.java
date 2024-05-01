package io.github.ngspace.hudder.meta.elements;

import static io.github.ngspace.hudder.Hudder.renderText;

import net.minecraft.client.gui.DrawContext;

public class TextElement extends Element {
	
	private static final long serialVersionUID = -7265671672610205526L;
	
	public final String text;
	public final int x;
	public final int y;
	public final int color;
	public final int backgroundcolor;
	public final float scale;
	public final boolean shadow;
	public final boolean background;
    public static final String NL_REGEX = "\r?\n";

	public TextElement(int x, int y, String text, float scale, int color, boolean shadow,boolean background,
			int backgroundcolor) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.color = color;
		this.backgroundcolor = backgroundcolor;
		this.scale = scale;
		this.shadow = shadow;
		this.background = background;
	}
	@Override public void RenderElement(DrawContext context, float delta) {
		renderText(context, text, x, y, color, scale, shadow, background, backgroundcolor);
	}
}