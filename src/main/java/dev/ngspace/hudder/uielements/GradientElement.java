package dev.ngspace.hudder.uielements;

import org.joml.Matrix3x2fStack;

import dev.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class GradientElement extends AUIElement {

	final int x;
	final int y;
	final int width;
	final int height;
	final int color1;
	final int color2;
	final boolean horizontal;
	
	public GradientElement(int x, int y, int width, int height, int color1, int color2, boolean horizontal) {
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
        if (horizontal) {
            Matrix3x2fStack matrixStack = graphics.pose();
            matrixStack.pushMatrix();
            matrixStack.translate(x, y);
            matrixStack.rotate(1.570796f);
            matrixStack.translate(-x, -y);
    		graphics.fillGradient(x, y-width, x+height, y, color1, color2);
            matrixStack.popMatrix();
        } else {
    		graphics.fillGradient(x, y, x+width, y+height, color1, color2);
        }
	}
	
}
