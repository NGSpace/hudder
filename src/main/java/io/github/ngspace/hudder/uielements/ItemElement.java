package io.github.ngspace.hudder.uielements;

import io.github.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class ItemElement extends AUIElement {
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
	
	@Override
	public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {
		var ins = Minecraft.getInstance();
        var matrixStack = context.pose();
        if (scale!=1f) {
            matrixStack.pushPose();
            matrixStack.translate(x, y, 0f);
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(-x, -y, 0f);
        	context.renderItem(stack, (int)x, (int)y);
        	if (showcount) context.renderItemDecorations(ins.font,stack, (int)x, (int)y);
            matrixStack.popPose();
        } else {
        	context.renderItem(stack, (int)x, (int)y);
        	if (showcount) context.renderItemDecorations(ins.font,stack, (int)x, (int)y);
        }
	}
}