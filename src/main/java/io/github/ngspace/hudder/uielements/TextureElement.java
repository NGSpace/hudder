package io.github.ngspace.hudder.uielements;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.ngspace.hudder.hudder.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class TextureElement extends AUIElement {
	
	public final int x;
	public final int y;
	public final int width;
	public final int height;
	public final ResourceLocation id;
	
	public TextureElement(ResourceLocation id, int x, int y, int width, int height) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.id=id;
	}
	
	@Override public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		context.blit(RenderType::guiTextured,id, x, y, 0, 0f, width, height, width, height);
		RenderSystem.disableBlend();
	}
}