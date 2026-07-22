package dev.ngspace.ngsmcconfig.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * Hex edit box with a clickable ARGB swatch and an overlay color picker.
 * 
 * Yes, AI was used to generate this in it's entirety. Because I am NOT doing this shit.
 */
public class NGSMCConfigColorPickerWidget extends EditBox implements NGSMCConfigOverlayWidget {

	private static final int SWATCH_WIDTH = 20;
	private static final int CHECKER_SIZE = 4;
	private static final int PANEL_WIDTH = 176;
	private static final int PANEL_HEIGHT = 125;
	private static final int PANEL_PADDING = 6;
	private static final int SATURATION_VALUE_WIDTH = 140;
	private static final int SATURATION_VALUE_HEIGHT = 90;
	private static final int HUE_WIDTH = 18;
	private static final int ALPHA_HEIGHT = 14;
	private static final int CONTROL_GAP = 5;
	private static final int BORDER_COLOR = 0xFFFFFFFF;
	private static final int PANEL_BACKGROUND = 0xFF101010;

	private int color;
	private float hue;
	private float saturation;
	private float brightness;
	private boolean open;
	private boolean applyingPickerColor;
	private DragTarget dragTarget = DragTarget.NONE;

	public NGSMCConfigColorPickerWidget(Font font, int x, int y, int width, int height, Component message,
			int color) {
		super(font, x, y, width, height, message);
		setColor(color);
	}

	public int getColor() {
		return color;
	}

	public boolean isApplyingPickerColor() {
		return applyingPickerColor;
	}

	/**
	 * Updates the swatch and picker controls without changing the edit-box text.
	 */
	public void setColor(int color) {
		this.color = color;
		float[] hsv = rgbToHsv(color);
		this.hue = hsv[0];
		this.saturation = hsv[1];
		this.brightness = hsv[2];
	}

	/**
	 * Updates both the picker state and its #AARRGGBB text.
	 */
	public void setColorAndText(int color) {
		setColor(color);
		setValue(formatColor(color));
	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		graphics.enableScissor(getX(), getY(), getRight() - SWATCH_WIDTH, getBottom());
		super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
		graphics.disableScissor();

		int swatchX = getRight() - SWATCH_WIDTH;
		drawCheckerboard(graphics, swatchX, getY(), SWATCH_WIDTH, getHeight());
		graphics.fill(swatchX, getY(), getRight(), getBottom(), color);
		drawBorder(graphics, swatchX, getY(), SWATCH_WIDTH, getHeight(), open ? 0xFFFFFFFF : 0xFF9C9C9C);

		if (contains(mouseX, mouseY, swatchX, getY(), SWATCH_WIDTH, getHeight()))
			graphics.requestCursor(CursorTypes.POINTING_HAND);
	}

	@Override
	public void extractOverlayRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick,
			int overlayTop, int overlayBottom) {
		if (!open)
			return;

		int panelX = getPanelX();
		int panelY = getPanelY(overlayTop, overlayBottom);
		drawBox(graphics, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, PANEL_BACKGROUND, BORDER_COLOR);

		int svX = panelX + PANEL_PADDING;
		int svY = panelY + PANEL_PADDING;
		renderSaturationValueArea(graphics, svX, svY);

		int hueX = svX + SATURATION_VALUE_WIDTH + CONTROL_GAP;
		renderHueSlider(graphics, hueX, svY);

		int alphaX = svX;
		int alphaY = svY + SATURATION_VALUE_HEIGHT + 8;
		int alphaWidth = PANEL_WIDTH - PANEL_PADDING * 2;
		renderAlphaSlider(graphics, alphaX, alphaY, alphaWidth);

		if (contains(mouseX, mouseY, svX, svY, SATURATION_VALUE_WIDTH, SATURATION_VALUE_HEIGHT)
				|| contains(mouseX, mouseY, hueX, svY, HUE_WIDTH, SATURATION_VALUE_HEIGHT)
				|| contains(mouseX, mouseY, alphaX, alphaY, alphaWidth, ALPHA_HEIGHT))
			graphics.requestCursor(CursorTypes.POINTING_HAND);
	}

	@Override
	public boolean mouseClickedOverlay(MouseButtonEvent event, boolean doubleClick, int overlayTop, int overlayBottom) {
		if (!active || !visible || !isValidClickButton(event.buttonInfo()))
			return false;

		double mouseX = event.x();
		double mouseY = event.y();
		int swatchX = getRight() - SWATCH_WIDTH;
		if (contains(mouseX, mouseY, swatchX, getY(), SWATCH_WIDTH, getHeight())) {
			playDownSound(Minecraft.getInstance().getSoundManager());
			open = !open;
			dragTarget = DragTarget.NONE;
			return true;
		}

		if (!open)
			return false;

		int panelX = getPanelX();
		int panelY = getPanelY(overlayTop, overlayBottom);
		int svX = panelX + PANEL_PADDING;
		int svY = panelY + PANEL_PADDING;
		int hueX = svX + SATURATION_VALUE_WIDTH + CONTROL_GAP;
		int alphaX = svX;
		int alphaY = svY + SATURATION_VALUE_HEIGHT + 8;
		int alphaWidth = PANEL_WIDTH - PANEL_PADDING * 2;

		if (contains(mouseX, mouseY, svX, svY, SATURATION_VALUE_WIDTH, SATURATION_VALUE_HEIGHT)) {
			dragTarget = DragTarget.SATURATION_VALUE;
			updateFromMouse(mouseX, mouseY, panelX, panelY);
			return true;
		}
		if (contains(mouseX, mouseY, hueX, svY, HUE_WIDTH, SATURATION_VALUE_HEIGHT)) {
			dragTarget = DragTarget.HUE;
			updateFromMouse(mouseX, mouseY, panelX, panelY);
			return true;
		}
		if (contains(mouseX, mouseY, alphaX, alphaY, alphaWidth, ALPHA_HEIGHT)) {
			dragTarget = DragTarget.ALPHA;
			updateFromMouse(mouseX, mouseY, panelX, panelY);
			return true;
		}
		if (contains(mouseX, mouseY, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT))
			return true;

		closeOverlay();
		return false;
	}

	@Override
	public boolean mouseDraggedOverlay(MouseButtonEvent event, double dragX, double dragY, int overlayTop,
			int overlayBottom) {
		if (!open || dragTarget == DragTarget.NONE)
			return false;
		updateFromMouse(event.x(), event.y(), getPanelX(), getPanelY(overlayTop, overlayBottom));
		return true;
	}

	@Override
	public boolean mouseReleasedOverlay(MouseButtonEvent event, int overlayTop, int overlayBottom) {
		if (dragTarget == DragTarget.NONE)
			return false;
		dragTarget = DragTarget.NONE;
		return true;
	}

	private void updateFromMouse(double mouseX, double mouseY, int panelX, int panelY) {
		int svX = panelX + PANEL_PADDING;
		int svY = panelY + PANEL_PADDING;
		int alphaWidth = PANEL_WIDTH - PANEL_PADDING * 2;

		switch (dragTarget) {
			case SATURATION_VALUE -> {
				saturation = clampUnit((float) ((mouseX - svX) / (SATURATION_VALUE_WIDTH - 1.0)));
				brightness = 1.0F - clampUnit((float) ((mouseY - svY) / (SATURATION_VALUE_HEIGHT - 1.0)));
			}
			case HUE -> hue = clampUnit((float) ((mouseY - svY) / (SATURATION_VALUE_HEIGHT - 1.0)));
			case ALPHA -> {
				int alpha = Mth.clamp((int) Math.round((mouseX - svX) * 255.0 / (alphaWidth - 1.0)), 0, 255);
				applyPickerColor(alpha);
				return;
			}
			case NONE -> {
				return;
			}
		}
		applyPickerColor(getValueAlpha());
	}

	private void applyPickerColor(int alpha) {
		color = hsvToArgb(hue, saturation, brightness, alpha);
		applyingPickerColor = true;
		try {
			setValue(formatColor(color));
		} finally {
			applyingPickerColor = false;
		}
	}

	private void renderSaturationValueArea(GuiGraphicsExtractor graphics, int x, int y) {
		for (int drawY = 0; drawY < SATURATION_VALUE_HEIGHT; drawY += 2) {
			float value = 1.0F - drawY / (float) (SATURATION_VALUE_HEIGHT - 1);
			int cellHeight = Math.min(2, SATURATION_VALUE_HEIGHT - drawY);
			for (int drawX = 0; drawX < SATURATION_VALUE_WIDTH; drawX += 2) {
				float sat = drawX / (float) (SATURATION_VALUE_WIDTH - 1);
				int cellWidth = Math.min(2, SATURATION_VALUE_WIDTH - drawX);
				graphics.fill(x + drawX, y + drawY, x + drawX + cellWidth, y + drawY + cellHeight,
						hsvToArgb(hue, sat, value, 255));
			}
		}
		drawBorder(graphics, x, y, SATURATION_VALUE_WIDTH, SATURATION_VALUE_HEIGHT, BORDER_COLOR);

		int markerX = x + Math.round(saturation * (SATURATION_VALUE_WIDTH - 1));
		int markerY = y + Math.round((1.0F - brightness) * (SATURATION_VALUE_HEIGHT - 1));
		drawMarker(graphics, markerX, markerY);
	}

	private void renderHueSlider(GuiGraphicsExtractor graphics, int x, int y) {
		for (int drawY = 0; drawY < SATURATION_VALUE_HEIGHT; drawY += 2) {
			float sliderHue = drawY / (float) (SATURATION_VALUE_HEIGHT - 1);
			int cellHeight = Math.min(2, SATURATION_VALUE_HEIGHT - drawY);
			graphics.fill(x, y + drawY, x + HUE_WIDTH, y + drawY + cellHeight,
					hsvToArgb(sliderHue, 1.0F, 1.0F, 255));
		}
		drawBorder(graphics, x, y, HUE_WIDTH, SATURATION_VALUE_HEIGHT, BORDER_COLOR);
		int markerY = y + Math.round(hue * (SATURATION_VALUE_HEIGHT - 1));
		graphics.fill(x - 1, markerY - 1, x + HUE_WIDTH + 1, markerY, 0xFF000000);
		graphics.fill(x - 1, markerY, x + HUE_WIDTH + 1, markerY + 1, 0xFFFFFFFF);
	}

	private void renderAlphaSlider(GuiGraphicsExtractor graphics, int x, int y, int width) {
		drawCheckerboard(graphics, x, y, width, ALPHA_HEIGHT);
		int opaqueRgb = hsvToArgb(hue, saturation, brightness, 255) & 0x00FFFFFF;
		for (int drawX = 0; drawX < width; drawX += 2) {
			int alpha = Math.round(drawX * 255.0F / (width - 1));
			int cellWidth = Math.min(2, width - drawX);
			graphics.fill(x + drawX, y, x + drawX + cellWidth, y + ALPHA_HEIGHT, alpha << 24 | opaqueRgb);
		}
		drawBorder(graphics, x, y, width, ALPHA_HEIGHT, BORDER_COLOR);
		int markerX = x + Math.round(getValueAlpha() / 255.0F * (width - 1));
		graphics.fill(markerX - 1, y - 1, markerX, y + ALPHA_HEIGHT + 1, 0xFF000000);
		graphics.fill(markerX, y - 1, markerX + 1, y + ALPHA_HEIGHT + 1, 0xFFFFFFFF);
	}

	private void drawCheckerboard(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		for (int drawY = 0; drawY < height; drawY += CHECKER_SIZE) {
			for (int drawX = 0; drawX < width; drawX += CHECKER_SIZE) {
				int checkerColor = ((drawX / CHECKER_SIZE + drawY / CHECKER_SIZE) & 1) == 0
						? 0xFFB0B0B0 : 0xFF686868;
				graphics.fill(x + drawX, y + drawY, Math.min(x + width, x + drawX + CHECKER_SIZE),
						Math.min(y + height, y + drawY + CHECKER_SIZE), checkerColor);
			}
		}
	}

	private void drawMarker(GuiGraphicsExtractor graphics, int centerX, int centerY) {
		graphics.fill(centerX - 3, centerY - 1, centerX + 4, centerY + 2, 0xFF000000);
		graphics.fill(centerX - 1, centerY - 3, centerX + 2, centerY + 4, 0xFF000000);
		graphics.fill(centerX - 2, centerY, centerX + 3, centerY + 1, 0xFFFFFFFF);
		graphics.fill(centerX, centerY - 2, centerX + 1, centerY + 3, 0xFFFFFFFF);
	}

	private void drawBox(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int background,
			int border) {
		graphics.fill(x, y, x + width, y + height, background);
		drawBorder(graphics, x, y, width, height, border);
	}

	private void drawBorder(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int border) {
		graphics.fill(x, y, x + width, y + 1, border);
		graphics.fill(x, y + height - 1, x + width, y + height, border);
		graphics.fill(x, y, x + 1, y + height, border);
		graphics.fill(x + width - 1, y, x + width, y + height, border);
	}

	private int getPanelX() {
		return getRight() - PANEL_WIDTH;
	}

	private int getPanelY(int overlayTop, int overlayBottom) {
		int availableBelow = overlayBottom - getBottom();
		int availableAbove = getY() - overlayTop;
		return availableBelow >= PANEL_HEIGHT || availableBelow >= availableAbove
				? getBottom() : getY() - PANEL_HEIGHT;
	}

	private int getValueAlpha() {
		return color >>> 24;
	}

	private static String formatColor(int color) {
		return "#" + String.format("%1$08X", color);
	}

	private static float clampUnit(float value) {
		return Mth.clamp(value, 0.0F, 1.0F);
	}

	private static boolean contains(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	private static float[] rgbToHsv(int argb) {
		float red = ((argb >> 16) & 0xFF) / 255.0F;
		float green = ((argb >> 8) & 0xFF) / 255.0F;
		float blue = (argb & 0xFF) / 255.0F;
		float max = Math.max(red, Math.max(green, blue));
		float min = Math.min(red, Math.min(green, blue));
		float delta = max - min;

		float hue;
		if (delta == 0.0F)
			hue = 0.0F;
		else if (max == red)
			hue = ((green - blue) / delta) % 6.0F;
		else if (max == green)
			hue = (blue - red) / delta + 2.0F;
		else
			hue = (red - green) / delta + 4.0F;
		hue /= 6.0F;
		if (hue < 0.0F)
			hue += 1.0F;

		float saturation = max == 0.0F ? 0.0F : delta / max;
		return new float[] { hue, saturation, max };
	}

	private static int hsvToArgb(float hue, float saturation, float brightness, int alpha) {
		float wrappedHue = hue - (float) Math.floor(hue);
		float chroma = brightness * saturation;
		float hueSection = wrappedHue * 6.0F;
		float x = chroma * (1.0F - Math.abs(hueSection % 2.0F - 1.0F));
		float red;
		float green;
		float blue;

		if (hueSection < 1.0F) {
			red = chroma;
			green = x;
			blue = 0.0F;
		} else if (hueSection < 2.0F) {
			red = x;
			green = chroma;
			blue = 0.0F;
		} else if (hueSection < 3.0F) {
			red = 0.0F;
			green = chroma;
			blue = x;
		} else if (hueSection < 4.0F) {
			red = 0.0F;
			green = x;
			blue = chroma;
		} else if (hueSection < 5.0F) {
			red = x;
			green = 0.0F;
			blue = chroma;
		} else {
			red = chroma;
			green = 0.0F;
			blue = x;
		}

		float match = brightness - chroma;
		int redByte = Mth.clamp(Math.round((red + match) * 255.0F), 0, 255);
		int greenByte = Mth.clamp(Math.round((green + match) * 255.0F), 0, 255);
		int blueByte = Mth.clamp(Math.round((blue + match) * 255.0F), 0, 255);
		return Mth.clamp(alpha, 0, 255) << 24 | redByte << 16 | greenByte << 8 | blueByte;
	}

	@Override
	public boolean isOverlayOpen() {
		return open;
	}

	@Override
	public void closeOverlay() {
		open = false;
		dragTarget = DragTarget.NONE;
	}

	private enum DragTarget {
		NONE,
		SATURATION_VALUE,
		HUE,
		ALPHA
	}
}
