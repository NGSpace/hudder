package dev.ngspace.hudder.uielements.minecraft;

import dev.ngspace.hudder.main.HudderRenderer;
import dev.ngspace.hudder.uielements.AUIElement;
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
	
	public ItemElement(float x, float y, ItemStack stack, float scale, boolean showcount) {
		this.stack = stack;
		this.x = x;
		this.y = y;
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