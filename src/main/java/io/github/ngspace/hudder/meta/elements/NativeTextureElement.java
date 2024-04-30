package io.github.ngspace.hudder.meta.elements;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

public class NativeTextureElement extends Element {
	private static final long serialVersionUID = -4338632847255047504L;
	public final int x;
	public final int y;
	public final int width;
	public final int height;
	public final Identifier id;
	public final Sprite sprite;
	public NativeTextureElement(Identifier id, int x, int y, int width, int height) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.id=id;
		sprite = null;
		//Hudder.log(id.toString());
//		var sc = new SpriteContents(id,new SpriteDimensions(width, height),img,new Builder().build());
//		sprite = new Sprite(id, sc, width, height, 0, 0){};// Cheating in a protected constructor ;)
	}
	@Override public void RenderElement(DrawContext context, float delta) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		context.drawTexture(id, x, y, 1, 0f, 0f, width, height, width, height);
		RenderSystem.disableBlend();
	}
}