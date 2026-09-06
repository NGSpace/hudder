package dev.ngspace.hudder.uielements.primitives;

import org.joml.Matrix3x2fStack;

import dev.ngspace.hudder.main.HudderRenderer;
import dev.ngspace.hudder.uielements.AUIElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class GradientElement extends AUIElement {

	final float x;
	final float y;
	final float width;
	final float height;
	final int color1;
	final int color2;
	final boolean horizontal;
	
	public GradientElement(float x, float y, float width, float height, int color1, int color2, boolean horizontal) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color1 = color1;
		this.color2 = color2;
		this.horizontal = horizontal;
	}

	@Override
	public void renderElement(GuiGraphicsExtractor graphics, HudderRenderer renderer, DeltaTracker delta) {
        Matrix3x2fStack matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(x, y);
        if (horizontal)
        	matrixStack.rotate(1.570796f);
        graphics.pose().scale(width, height);
		graphics.fillGradient(0, 0, 1, 1, color1, color2);
        matrixStack.popMatrix();
	}
	
}
