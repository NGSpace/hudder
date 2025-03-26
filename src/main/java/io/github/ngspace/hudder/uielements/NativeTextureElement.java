package io.github.ngspace.hudder.uielements;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;

public class NativeTextureElement extends AUIElement {
	
	public final int x;
	public final int y;
	public final int width;
	public final int height;
	public final ResourceLocation id;
    public static Minecraft mc = Minecraft.getInstance();
	
	public NativeTextureElement(ResourceLocation id, int x, int y, int width, int height) throws CompileException {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.id=id;
		if (mc.getTextureManager().getTexture(id) instanceof SimpleTexture)
			throw new CompileException("Native texture not found: " + id.toString());
	}
	
	@Override public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		context.blit(RenderType::guiTextured,id, x, y, 0, 0f, width, height, width, height);
		RenderSystem.disableBlend();
	}
}