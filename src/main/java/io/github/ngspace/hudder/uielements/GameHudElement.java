package io.github.ngspace.hudder.uielements;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.ngspace.hudder.main.HudderRenderer;
import io.github.ngspace.hudder.mixin.InGameHudAccessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.PlayerRideableJumping;

/**
 * This element is a merging of all builtin GUI elements (Status bars) 
 * with the goal of allowing the creation of a custom
 * game hud without forcing the user to reimplement every single UI Element themselves
 */
public class GameHudElement extends AUIElement {
	
	protected static Minecraft mc = Minecraft.getInstance();
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
		try {
			InGameHudAccessor acchud = (InGameHudAccessor) (mc.gui);
			float scaledWidth = context.guiWidth();
	        float scaledHeight = context.guiHeight();
	        PoseStack matrixStack = context.pose();
	        matrixStack.pushPose();
	        switch (type) {
				case STATUS_BARS:
			        if (mc.gameMode.canHurtPlayer()) {
				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 39, 0f);
				        acchud.callRenderPlayerHealth(context);
				        acchud.callRenderVehicleHealth(context);
			        }
			        break;
				case EXP_AND_MOUNT_BAR:
		            int i = (int) (scaledWidth / 2 - 91);
			    	PlayerRideableJumping jumpingMount = mc.player.jumpableVehicle();
		            if (jumpingMount != null) {
				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 39, 0f);
				        acchud.callRenderJumpMeter(jumpingMount, context, i);
		            } else if (mc.gameMode.hasExperience()) {
				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 35, 0f);
				        Font font = mc.font;
			    		int jj = mc.player.experienceLevel;
			    		if (mc.gameMode.hasExperience() && jj > 0) {
			    			String string = "" + jj;
			    			int j = (context.guiWidth() - font.width(string)) / 2;
			    			int k = context.guiHeight() - 31 - 8;
			    			context.drawString(font, string, j + 1, k, 0, false);
			    			context.drawString(font, string, j - 1, k, 0, false);
			    			context.drawString(font, string, j, k + 1, 0, false);
			    			context.drawString(font, string, j, k - 1, 0, false);
			    			context.drawString(font, string, j, k, 8453920, false);
			    		}
				        acchud.callRenderExperienceBar(context, i);
		            }
					break;
				case HOTBAR:
			        matrixStack.translate(x-scaledWidth/2, y-scaledHeight, 0f);
			        acchud.callRenderHotbarAndDecorations(context, delta);
					break;
				case ITEM_TOOLTIP:
					int tooltipy = 44;
					if (mc.gameMode.canHurtPlayer()) tooltipy -= 14;
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
