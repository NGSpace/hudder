package io.github.ngspace.hudder.uielements;

import org.apache.commons.lang3.tuple.Pair;

import io.github.ngspace.hudder.main.HudderRenderer;
import io.github.ngspace.hudder.mixin.InGameHudAccessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;

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
	        var matrixStack = context.pose();
	        matrixStack.pushMatrix();
	        switch (type) {
				case STATUS_BARS:
			        if (mc.gameMode.canHurtPlayer()) {
				        matrixStack.translate(x-scaledWidth/2, y-scaledHeight + 39);
				        acchud.callRenderPlayerHealth(context);
				        acchud.callRenderVehicleHealth(context);
			        }
			        break;
				case EXP_AND_MOUNT_BAR:
					Gui.ContextualInfo contextualInfo = acchud.nextContextualInfoState();
					var contextualInfoBar = acchud.contextualInfoBar();
					var contextualInfoBarRenderers = acchud.contextualInfoBarRenderers();
					if (contextualInfo != contextualInfoBar.getKey()) {
						contextualInfoBar = Pair.of(contextualInfo, contextualInfoBarRenderers.get(contextualInfo).get());
					}

					contextualInfoBar.getValue().renderBackground(context, delta);
					if (mc.gameMode.hasExperience() && mc.player.experienceLevel > 0) {
						ContextualBarRenderer.renderExperienceLevel(context, mc.font, mc.player.experienceLevel);
					}

					contextualInfoBar.getValue().render(context, delta);
					break;
				case HOTBAR:
			        matrixStack.translate(x-scaledWidth/2, y-scaledHeight);
			        acchud.callRenderHotbarAndDecorations(context, delta);
					break;
				case ITEM_TOOLTIP:
					int tooltipy = 44;
					if (mc.gameMode.canHurtPlayer()) tooltipy -= 14;
			        matrixStack.translate(x-scaledWidth/2, tooltipy-scaledHeight+y);
			        acchud.callRenderSelectedItemName(context);
					break;
				default:
					break;
			}
	        matrixStack.popMatrix();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
