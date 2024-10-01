package io.github.ngspace.hudder.meta.elements;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.mixin.InGameHudAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.JumpingMount;

/**
 * This element is a merging of all builtin GUI elements (Status bars) 
 * with the goal of allowing the creation of a custom
 * game hud without forcing the user to reimplement every single UI Element themselves
 */
public class GameHudElement extends Element {
	public enum GuiType {
		STATUS_BARS,
		EXP_AND_MOUNT_BAR,
		HOTBAR,
		ITEM_TOOLTIP;
	}
	
	private static final long serialVersionUID = 7928033432164989214L;
	
	public final int x;
	public final int y;
	public final GuiType type;
	
	public GameHudElement(int x, int y, GuiType type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}

	@Override public void RenderElement(DrawContext context, float delta) {
		try {
//			InGameHud hud = Hudder.ins.inGameHud;
//			InGameHudAccessor acchud = (InGameHudAccessor) (Hudder.ins.inGameHud);
//			float scaledWidth = context.getScaledWindowWidth();
//	        float scaledHeight = context.getScaledWindowHeight();
//	        MatrixStack matrixStack = context.getMatrices();
//	        matrixStack.push();
//	        switch (type) {
//				case STATUS_BARS:
//			        if (Hudder.ins.interactionManager.hasStatusBars()) {
//				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 39, 0f);
//				        acchud.callRenderStatusBars(context);
//				        acchud.callRenderMountHealth(context);
//			        }
//			        break;
//				case EXP_AND_MOUNT_BAR:
//		            int i = (int) (scaledWidth / 2 - 91);
//			    	JumpingMount jumpingMount = Hudder.ins.player.getJumpingMount();
//		            if (jumpingMount != null) {
//				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 39, 0f);
//				        hud.renderMountJumpBar(jumpingMount, context, i);
//		            } else if (Hudder.ins.interactionManager.hasExperienceBar()) {
//				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 35, 0f);
//				        hud.renderExperienceBar(context, i);
//		            }
//					break;
//				case HOTBAR:
//			        matrixStack.translate(x-scaledWidth/2, y-scaledHeight, 0f);
//			        acchud.callRenderHotbar(delta, context);
//					break;
//				case ITEM_TOOLTIP:
//			        matrixStack.translate(x-scaledWidth/2, y-scaledHeight+45, 0f);
//			        hud.renderHeldItemTooltip(context);
//					break;
//				default:
//					break;
//			}
//	        matrixStack.pop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
