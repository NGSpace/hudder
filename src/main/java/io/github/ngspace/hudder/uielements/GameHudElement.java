package io.github.ngspace.hudder.uielements;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.ngspace.hudder.main.HudderRenderer;
import io.github.ngspace.hudder.mixin.InGameHudAccessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * This element is a merging of all builtin GUI elements (Status bars) 
 * with the goal of allowing the creation of a custom
 * game hud without forcing the user to reimplement every single UI Element themselves
 */
public class GameHudElement extends AUIElement {
	public enum GuiType {
		STATUS_BARS,
		EXP_AND_MOUNT_BAR,
		HOTBAR,
		ITEM_TOOLTIP;
	}
	
	public final int x;
	public final int y;
	public final GuiType type;
	
	public GameHudElement(int x, int y, GuiType type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}

	@Override public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {
		Minecraft ins = Minecraft.getInstance();
		try {
			InGameHudAccessor acchud = (InGameHudAccessor) (ins.gui);
			float scaledWidth = ins.getWindow().getGuiScaledWidth();
	        float scaledHeight = ins.getWindow().getGuiScaledHeight();
	        PoseStack matrixStack = context.pose();
	        matrixStack.pushPose();
	        switch (type) {
				case STATUS_BARS:
			        if (ins.gameMode.canHurtPlayer()) {
				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 39, 0f);
				        acchud.callRenderPlayerHealth(context);
			        }
			        break;
				case EXP_AND_MOUNT_BAR:
		            int i = (int) (scaledWidth / 2 - 91);
		            var jumpingMount = ins.player.jumpableVehicle();
		            if (jumpingMount != null) {
				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 39, 0f);
				        acchud.callRenderJumpMeter(jumpingMount, context, i);
		            } else if (ins.gameMode.hasExperience()) {
				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 35, 0f);
				        Font textRenderer = ins.font;
			    		int jj = ins.player.experienceLevel;
			    		if (ins.gameMode.hasExperience() && jj > 0) {
			    			ins.getProfiler().push("expLevel");
			    			String string = "" + jj;
			    			int j = (ins.getWindow().getGuiScaledWidth() - textRenderer.width(string)) / 2;
			    			int k = ins.getWindow().getGuiScaledHeight() - 31 - 8;
			    			context.drawString(textRenderer, string, j + 1, k, 0, false);
			    			context.drawString(textRenderer, string, j - 1, k, 0, false);
			    			context.drawString(textRenderer, string, j, k + 1, 0, false);
			    			context.drawString(textRenderer, string, j, k - 1, 0, false);
			    			context.drawString(textRenderer, string, j, k, 8453920, false);
			    			ins.getProfiler().pop();
			    		}
				        acchud.callRenderExperienceBar(context, i);
		            }
					break;
				case HOTBAR:
			        matrixStack.translate(x-scaledWidth/2, y-scaledHeight, 0f);
			        acchud.callRenderItemHotbar(context, delta);
					break;
				case ITEM_TOOLTIP:
					int tooltipy =  59;
					if (!ins.gameMode.canHurtPlayer()) tooltipy -= 14;
			        matrixStack.translate(x-scaledWidth/2, tooltipy-scaledHeight+y, 0f);
			        acchud.callRenderSelectedItemName(context);
					break;
				default:
					break;
			}
	        matrixStack.popPose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
