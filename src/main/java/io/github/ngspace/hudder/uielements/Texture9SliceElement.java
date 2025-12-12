package io.github.ngspace.hudder.uielements;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.main.HudderRenderer;
import io.github.ngspace.hudder.utils.HudFileUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public class Texture9SliceElement extends AUIElement {

	public final int x;
	public final int y;
	public final int width;
	public final int height;
	public final Identifier id;
	private float[] scales;
	
	public Texture9SliceElement(String filename, int x, int y, int width, int height, float[] slices)
			throws CompileException {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.id=HudFileUtils.getTexture(filename);
		this.scales = slices;
		if (slices.length!=4)
			throw new CompileException("Slices array must have 4 values: [left, right, top, bottom]!");
		if (!HudFileUtils.imageLoaded(id)) 
			throw new CompileException("Image not loaded (Or file is not a valid image): " + filename);
	}

	@Override public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {
		renderer.renderTexture9Slice(context, id, x, y, width, height, scales);
	}
}