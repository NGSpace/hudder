package io.github.ngspace.hudder.methods.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class ItemElement extends AUIElement {
	private static final long serialVersionUID = -4033666012277014500L;
	public final ItemStack stack;
	public final double x;
	public final double y;
	public final boolean showcount;
	public final float scale;
	public ItemElement(double x, double y, ItemStack stack, float scale, boolean showcount) {
		this.stack = stack;
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.showcount = showcount;
	}
	static TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
	
	@Override
	public void renderElement(DrawContext context, RenderTickCounter delta) {
        MatrixStack matrixStack = context.getMatrices();
        if (scale!=1f) {
            matrixStack.push();
            matrixStack.translate(x, y, 0);
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(-x, -y, 0);
        	context.drawItem(stack, (int)x, (int)y);
        	if (showcount) context.drawStackOverlay(textRenderer, stack, (int)x, (int)y);
            matrixStack.pop();
        } else {
        	context.drawItem(stack, (int)x, (int)y);
        	if (showcount) context.drawStackOverlay(textRenderer, stack, (int)x, (int)y);
        }
	}
}