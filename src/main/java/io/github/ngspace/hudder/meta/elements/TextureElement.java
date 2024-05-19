package io.github.ngspace.hudder.meta.elements;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class TextureElement extends Element {
	private static final long serialVersionUID = -3008870889825976036L;
	public final int x;
	public final int y;
	public final int width;
	public final int height;
	public final Identifier id;
	public TextureElement(Identifier id, int x, int y, int width, int height) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.id=id;
	}
	@Override public void renderElement(DrawContext context, float delta) {
		context.drawTexture(id, x, y, 0, 0, 0, width, height, height, height);
	}
}