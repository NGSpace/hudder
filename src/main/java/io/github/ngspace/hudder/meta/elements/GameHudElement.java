package io.github.ngspace.hudder.meta.elements;

import static io.github.ngspace.hudder.Hudder.ins;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.mixin.InGameHudAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
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

	@Override public void renderElement(DrawContext context, RenderTickCounter delta) {
		try {
			InGameHudAccessor acchud = (InGameHudAccessor) (Hudder.ins.inGameHud);
			float scaledWidth = context.getScaledWindowWidth();
	        float scaledHeight = context.getScaledWindowHeight();
	        MatrixStack matrixStack = context.getMatrices();
	        matrixStack.push();
	        switch (type) {
				case STATUS_BARS:
			        if (Hudder.ins.interactionManager.hasStatusBars()) {
				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 39, 0f);
				        acchud.callRenderStatusBars(context);
				        acchud.callRenderMountHealth(context);
			        }
			        break;
				case EXP_AND_MOUNT_BAR:
		            int i = (int) (scaledWidth / 2 - 91);
			    	JumpingMount jumpingMount = Hudder.ins.player.getJumpingMount();
		            if (jumpingMount != null) {
				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 39, 0f);
				        acchud.callRenderMountJumpBar(jumpingMount, context, i);
		            } else if (Hudder.ins.interactionManager.hasExperienceBar()) {
				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 35, 0f);
				        TextRenderer textRenderer = ins.textRenderer;
			    		int jj = ins.player.experienceLevel;
			    		if (ins.interactionManager.hasExperienceBar() && jj > 0) {
			    			ins.getProfiler().push("expLevel");
			    			String string = "" + jj;
			    			int j = (context.getScaledWindowWidth() - textRenderer.getWidth(string)) / 2;
			    			int k = context.getScaledWindowHeight() - 31 - 8;
			    			context.drawText(textRenderer, string, j + 1, k, 0, false);
			    			context.drawText(textRenderer, string, j - 1, k, 0, false);
			    			context.drawText(textRenderer, string, j, k + 1, 0, false);
			    			context.drawText(textRenderer, string, j, k - 1, 0, false);
			    			context.drawText(textRenderer, string, j, k, 8453920, false);
			    			ins.getProfiler().pop();
			    		}
				        acchud.callRenderExperienceBar(context, i);
		            }
					break;
				case HOTBAR:
			        matrixStack.translate(x-scaledWidth/2, y-scaledHeight, 0f);
			        acchud.callRenderHotbar(context, delta);
					break;
				case ITEM_TOOLTIP:
					int tooltipy =  59;
					if (!ins.interactionManager.hasStatusBars()) tooltipy -= 14;
			        matrixStack.translate(x-scaledWidth/2, tooltipy-scaledHeight+y, 0f);
			        acchud.callRenderHeldItemTooltip(context);
					break;
				default:
					break;
			}
	        matrixStack.pop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
