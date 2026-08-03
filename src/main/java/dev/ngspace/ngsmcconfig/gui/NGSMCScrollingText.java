package dev.ngspace.ngsmcconfig.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class NGSMCScrollingText {

	public static final int ICON_TEXT_X_OFFSET = 20;
	public static final int PLAIN_TEXT_X_OFFSET = 2;
	public static final int TEXT_RIGHT_PADDING = 4;
	public static final int MARQUEE_GAP = 16;
	public static final long MARQUEE_DELAY_MS = 1000L;
	public static final float MARQUEE_SPEED = 20F;

	private NGSMCScrollingText() {}

	public static void render(GuiGraphicsExtractor graphics, Component text, int textX, int y, int textRight,
			int height, int color, long marqueeStartTime) {
		render(graphics, text, textX, y, textRight, height, color, marqueeStartTime, false);
	}

	public static void render(GuiGraphicsExtractor graphics, Component text, int textX, int y, int textRight,
			int height, int color, long marqueeStartTime, boolean centerText) {
		render(graphics, text, textX, y, textRight, height, color, marqueeStartTime, centerText, textX);
	}

	public static void render(GuiGraphicsExtractor graphics, Component text, int textX, int y, int textRight,
			int height, int color, long marqueeStartTime, boolean centerText, int scrollingTextX) {
		if (textRight <= textX || textRight <= scrollingTextX || height <= 0)
			return;

		Font font = Minecraft.getInstance().font;
		int availableWidth = textRight - textX;
		int renderedTextWidth = font.width(text);
		int textWidth = renderedTextWidth - 1;
		int drawX = centerText ? textX + (availableWidth - renderedTextWidth) / 2 : textX;
		int drawY = y + (height - font.lineHeight) / 2;
		boolean fitsWithoutScrolling = textWidth <= availableWidth && drawX >= scrollingTextX;

		graphics.enableScissor(scrollingTextX, y, textRight, y + height);
		if (fitsWithoutScrolling) {
			graphics.text(font, text, drawX, drawY, color);
		} else {
			long scrollingTime = Math.max(0L,
					System.currentTimeMillis() - marqueeStartTime - MARQUEE_DELAY_MS);
			int cycleWidth = textWidth + MARQUEE_GAP;
			int offset = (int) ((scrollingTime * MARQUEE_SPEED / 1_000.0F) % cycleWidth);
			drawX = scrollingTextX - offset;

			graphics.text(font, text, drawX, drawY, color);
			graphics.text(font, text, drawX + cycleWidth, drawY, color);
		}
		graphics.disableScissor();
	}
}
