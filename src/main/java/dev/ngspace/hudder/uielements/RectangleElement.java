package dev.ngspace.hudder.uielements;

import dev.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class RectangleElement extends AUIElement {

	final int x;
	final int y;
	final int width;
	final int height;
	final int color;
	
	public RectangleElement(int x, int y, int width, int height, int color) {
		super();
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
