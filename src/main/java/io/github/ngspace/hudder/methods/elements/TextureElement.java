package io.github.ngspace.hudder.methods.elements;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.ngspace.hudder.util.HudderRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class TextureElement extends AUIElement {
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
	@Override public void renderElement(DrawContext context, HudderRenderer renderer, RenderTickCounter delta) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		context.drawTexture(RenderLayer::getGuiTexturedOverlay,id, x, y, 0, 0f, width, height, width, height);
		RenderSystem.disableBlend();
	}
}