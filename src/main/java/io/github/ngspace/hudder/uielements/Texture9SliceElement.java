package io.github.ngspace.hudder.uielements;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class Texture9SliceElement extends AUIElement {

	public final int x;
	public final int y;
	public final int width;
	public final int height;
	public final ResourceLocation id;
	private float[] scales;
	
	public Texture9SliceElement(ResourceLocation id, int x, int y, int width, int height, float[] slices)
			throws CompileException {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.id=id;
		this.scales = slices;
		if (slices.length!=4) throw new CompileException("Slices array must have 4 values: [left, right, top, bottom]!");
	}

	@Override public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		renderer.renderTexture9Slice(context, id, x, y, width, height, scales);
		RenderSystem.disableBlend();
	}
}