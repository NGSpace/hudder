package io.github.ngspace.hudder.meta.elements;

import io.github.ngspace.hudder.Hudder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class ItemElement extends Element {
	private static final long serialVersionUID = -4033666012277014500L;
	public final ItemStack stack;
	public final double x1;
	public final double y1;
	public final boolean showcount;
	public final float scale;
	
	public ItemElement(double x, double y, ItemStack stack, float scale, boolean showcount) {
		this.stack = stack;
		this.x1 = x;
		this.y1 = y;
		this.scale = scale;
		this.showcount = showcount;
	}
	
	@Override
	public void RenderElement(DrawContext context, float delta) {
		float y = (float) y1;
		float x = (float) x1;
        MatrixStack matrixStack = context.getMatrices();
        if (scale!=1f) {
            matrixStack.push();
            matrixStack.translate(x, y, 0);
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(-x, -y, 0);
        	context.drawItem(stack, (int)x, (int)y);
        	if (showcount) context.drawItemInSlot(Hudder.ins.textRenderer,stack, (int)x, (int)y);
            matrixStack.pop();
        } else {
        	context.drawItem(stack, (int)x, (int)y);
        	if (showcount) context.drawItemInSlot(Hudder.ins.textRenderer,stack, (int)x, (int)y);
        }
	}
}
