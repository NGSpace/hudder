package dev.ngspace.ngsmcconfig.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class NGSMCScrollingText {

	public static final int ICON_TEXT_X_OFFSET = 20;
	public static final int PLAIN_TEXT_X_OFFSET = 2;
	public static final int TEXT_Y_OFFSET = 7;
	public static final int TEXT_RIGHT_PADDING = 4;
	public static final int MARQUEE_GAP = 16;
	public static final long MARQUEE_DELAY_MS = 1000L;
	public static final float MARQUEE_SPEED = 20F;

	private NGSMCScrollingText() {}

	public static void render(GuiGraphicsExtractor graphics, Component text, int textX, int y, int textRight,
			int height, int color, long marqueeStartTime) {
		if (textRight <= textX || height <= 0)
			return;

		Font font = Minecraft.getInstance().font;
		int availableWidth = textRight - textX;
		int textWidth = font.width(text)-1;

		graphics.enableScissor(textX, y, textRight, y + height);
		if (textWidth <= availableWidth) {
			graphics.text(font, text, textX, y + TEXT_Y_OFFSET, color);
		} else {
			long scrollingTime = Math.max(0L,
					System.currentTimeMillis() - marqueeStartTime - MARQUEE_DELAY_MS);
			int cycleWidth = textWidth + MARQUEE_GAP;
			int offset = (int) ((scrollingTime * MARQUEE_SPEED / 1_000.0F) % cycleWidth);
			int drawX = textX - offset;

			graphics.text(font, text, drawX, y + TEXT_Y_OFFSET, color);
			graphics.text(font, text, drawX + cycleWidth, y + TEXT_Y_OFFSET, color);
		}
		graphics.disableScissor();
	}
}
