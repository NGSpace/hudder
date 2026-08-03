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
	private int backgroundColor = 0;// Completely clear background
	private int focusedColor = 0x20FFFFFF;
	private int hoveredcolor = 0x20FFFFFF;
	private NGSMCConfigIcon disabledIcon;

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
	public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
    	int x = getX();
    	int width = getWidth();
    	int height = getHeight();
    	int y = getY();
    	
    	if (isHovered()) {
    		graphics.fill(x, y, x+width, y+height, hoveredcolor);
			graphics.requestCursor(CursorTypes.POINTING_HAND);
    	} else if (isFocused()) {
    		graphics.fill(x, y, x+width, y+height, focusedColor);
    	} else {
    		graphics.fill(x, y, x+width, y+height, backgroundColor);
    	}
    	
    	graphics.outline(x, y, width, height, outline);
    	
//    	System.out.println(height/16f);
    	
		int textX = (int) (x + (icon!=null ? NGSMCScrollingText.ICON_TEXT_X_OFFSET*(height/16f)
				: NGSMCScrollingText.PLAIN_TEXT_X_OFFSET));
		if (centerText) textX = x;
		int scrollingTextX = (int) (x + (icon!=null ? NGSMCScrollingText.ICON_TEXT_X_OFFSET*(height/16f)
				: NGSMCScrollingText.PLAIN_TEXT_X_OFFSET));
		int textRight = centerText ? x + width : x + width - NGSMCScrollingText.TEXT_RIGHT_PADDING;
		NGSMCScrollingText.render(graphics, getMessage(), textX, y+1, textRight, height, textcolor,
				marqueeStartTime, centerText, scrollingTextX);
    	if (icon!=null&&isActive())
    		icon.extractRenderState(graphics, mouseX, mouseY, a, height-4, height-4, x+2, y+2);
    	if (disabledIcon!=null&&!isActive())
    		disabledIcon.extractRenderState(graphics, mouseX, mouseY, a, height-4, height-4, x+2, y+2);
	}

	public void setCenterText(boolean centerText) {
		this.centerText = centerText;
	}

	public void setOutlineColor(int color) {
		this.outline = color;
	}
	
	public void setBackgroundColor(int color) {
		this.backgroundColor = color;
	}
	
	public void setFocusedBackgroundColor(int color) {
		this.focusedColor = color;
	}
	
	public void setHoveredBackgroundColor(int color) {
		this.hoveredcolor = color;
	}

	public void setDisabledIcon(NGSMCConfigIcon icon) {
		this.disabledIcon = icon;
	}

	public void setIcon(NGSMCConfigIcon icon) {
		this.icon = icon;
	}
	
	@Override
	public boolean shouldTakeFocusAfterInteraction() {
		return false;
	}
}
