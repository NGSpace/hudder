package dev.ngspace.ngsmcconfig.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * Implemented by widgets that draw interactive content outside their normal
 * row bounds. The options list renders this content after its regular entries
 * and gives it first chance to consume mouse clicks.
 */
public interface NGSMCConfigOverlayWidget {

	void extractOverlayRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick,
			int overlayTop, int overlayBottom);

	boolean mouseClickedOverlay(MouseButtonEvent event, boolean doubleClick, int overlayTop, int overlayBottom);

	default boolean mouseDraggedOverlay(MouseButtonEvent event, double dragX, double dragY, int overlayTop,
			int overlayBottom) {
		return false;
	}

	default boolean mouseReleasedOverlay(MouseButtonEvent event, int overlayTop, int overlayBottom) {
		return false;
	}

	boolean isOverlayOpen();

	void closeOverlay();
}
