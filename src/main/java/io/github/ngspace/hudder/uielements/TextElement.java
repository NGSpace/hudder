package io.github.ngspace.hudder.uielements;

import io.github.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public class TextElement extends AUIElement {
	
	public final String text;
	public final int x;
	public final int y;
	public final int color;
	public final int backgroundcolor;
	public final float scale;
	public final boolean shadow;
	public final boolean background;

	public TextElement(int x, int y, String text, float scale, int color, boolean shadow, boolean background,
			int backgroundcolor) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.color = color;
		this.backgroundcolor = backgroundcolor;
		this.scale = scale;
		this.shadow = shadow;
		this.background = background;
	}
	@Override public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {
		renderer.renderTextLine(context, text, x, y, color, scale, shadow, background, backgroundcolor);
	}
}