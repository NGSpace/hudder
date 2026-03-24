package dev.ngspace.hudder.uielements;

import dev.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public class ItemElement extends AUIElement {
	
	public final ItemStack stack;
	public final float x;
	public final float y;
	public final boolean showcount;
	public final float scale;
	static Font textRenderer = Minecraft.getInstance().font;
	
	public ItemElement(double x, double y, ItemStack stack, float scale, boolean showcount) {
		this.stack = stack;
		this.x = (float) x;
		this.y = (float) y;
		this.scale = scale;
		this.showcount = showcount;
	}
	
	@Override
	public void renderElement(GuiGraphicsExtractor context, HudderRenderer renderer, DeltaTracker delta) {
        if (scale!=1f) {
            var matrixStack = context.pose();
            matrixStack.pushMatrix();
            matrixStack.translate(x, y);
            matrixStack.scale(scale, scale);
            matrixStack.translate(-x, -y);
        	context.item(stack, (int)x, (int)y);
        	if (showcount) context.itemDecorations(textRenderer, stack, (int)x, (int)y);
            matrixStack.popMatrix();
        } else {
        	context.item(stack, (int)x, (int)y);
        	if (showcount) context.itemDecorations(textRenderer, stack, (int)x, (int)y);
        }
	}
}