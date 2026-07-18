package dev.ngspace.ngsmcconfig.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigIcon;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class NGSMCConfigButton extends Button {

	private NGSMCConfigIcon icon;
	private int textcolor;
	private int outline = 0; // Nothin
	private boolean centerText;
	private final long marqueeStartTime;

	public NGSMCConfigButton(int x, int y, int width, int height, Component message, OnPress onPress,
			int textcolor) {
		this(x, y, width, height, message, onPress, textcolor, null, false);
	}

	public NGSMCConfigButton(int x, int y, int width, int height, Component message, OnPress onPress,
			int textcolor, boolean centerText) {
		this(x, y, width, height, message, onPress, textcolor, null, centerText);
	}

	public NGSMCConfigButton(int x, int y, int width, int height, Component message, OnPress onPress,
			int textcolor, NGSMCConfigIcon icon) {
		this(x, y, width, height, message, onPress, textcolor, icon, false);
	}

	public NGSMCConfigButton(int x, int y, int width, int height, Component message, OnPress onPress,
			int textcolor, NGSMCConfigIcon icon, boolean centerText) {
		super(x, y, width, height, message, onPress, Button.DEFAULT_NARRATION);
		this.icon = icon;
		this.textcolor = textcolor;
		this.centerText = centerText;
		this.marqueeStartTime = System.currentTimeMillis();
	}

	@Override
	protected void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
    	int x = getX();
    	int width = getWidth();
    	int height = getHeight();
    	int y = getY();
    	
    	if (isHovered()) {
    		graphics.fill(x, y, x+width, y+height, 0x30FFFFFF);
			graphics.requestCursor(CursorTypes.POINTING_HAND);
    	}
    	if (isFocused()) {
    		graphics.fill(x+width-2, y, x+width, y+height, 0xFF00FFFF);
    	}
    	
    	graphics.outline(x, y, width, height, outline);
    	
		int textX = x + (icon!=null ? NGSMCScrollingText.ICON_TEXT_X_OFFSET
				: NGSMCScrollingText.PLAIN_TEXT_X_OFFSET);
		int textRight = x + width - NGSMCScrollingText.TEXT_RIGHT_PADDING;
		NGSMCScrollingText.render(graphics, getMessage(), textX, y, textRight, height, textcolor,
				marqueeStartTime, centerText);
    	if (icon!=null)
    		icon.extractRenderState(graphics, mouseX, mouseY, a, height-4, height-4, x+2, y+2);
    	
	}

	public void setCenterText(boolean centerText) {
		this.centerText = centerText;
	}

	public void setOutlineColor(int color) {
		this.outline = color;
	}
}
