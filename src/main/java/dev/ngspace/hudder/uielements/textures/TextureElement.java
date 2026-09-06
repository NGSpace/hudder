package dev.ngspace.hudder.uielements.textures;

import dev.ngspace.hudder.main.HudderRenderer;
import dev.ngspace.hudder.uielements.AUIElement;
import dev.ngspace.hudder.utils.HudFileUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class TextureElement extends AUIElement {
	
	public final float x;
	public final float y;
	public final float width;
	public final float height;
	public final Identifier id;
	
	public TextureElement(String filename, float x, float y, float width, float height) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.id=HudFileUtils.getTexture(filename);
		if (!HudFileUtils.imageLoaded(id)) 
			throw new IllegalArgumentException("Image not loaded (Or file is not a valid image): " + filename);
	}
	
	@Override public void renderElement(GuiGraphicsExtractor context, HudderRenderer renderer, DeltaTracker delta) {
        context.pose().pushMatrix();
        context.pose().translate(x, y);
        context.pose().scale(width, height);
		context.blit(RenderPipelines.GUI_TEXTURED,id, 0, 0, 0, 0, 1, 1, 1, 1);
//        context.fill(0, 0, 1, 1, argb);
        context.pose().popMatrix();
	}
}