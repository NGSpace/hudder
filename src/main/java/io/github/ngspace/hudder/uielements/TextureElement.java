package io.github.ngspace.hudder.uielements;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.main.HudderRenderer;
import io.github.ngspace.hudder.utils.HudFileUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class TextureElement extends AUIElement {
	
	public final int x;
	public final int y;
	public final int width;
	public final int height;
	public final ResourceLocation id;
	
	public TextureElement(String filename, int x, int y, int width, int height) throws CompileException {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.id=HudFileUtils.getTexture(filename);
		if (!HudFileUtils.imageLoaded(id)) 
			throw new CompileException("Image not loaded (Or file is not a valid image): " + filename);
	}
	
	@Override public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {
		context.blit(id, x, y, 0, 0f, width, height, width, height);
	}
}