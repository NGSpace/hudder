package dev.ngspace.hudder.uielements;

import dev.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class RectangleElement extends AUIElement {

	final float x;
	final float y;
	final float width;
	final float height;
	final int color;
	
	public RectangleElement(float x, float y, float width, float height, int color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
	}

	@Override
	public void renderElement(GuiGraphicsExtractor context, HudderRenderer renderer, DeltaTracker delta) {
		renderer.renderBlock(context, x, y, width, height, color);
	}
	
}
