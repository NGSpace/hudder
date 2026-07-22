package dev.ngspace.hudder.uielements;

import dev.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class TextElement extends AUIElement {
	
	public final Component component;
	public final int x;
	public final int y;
	public final int color;
	public final int backgroundcolor;
	public final float scale;
	public final boolean shadow;
	public final boolean background;

	/**
	 * @deprecated use {@link #TextElement(int, int, Component, float, int, boolean, boolean, int)}
	 */
	@Deprecated(since = "10.1.0", forRemoval = true)
	public TextElement(int x, int y, String text, float scale, int color, boolean shadow, boolean background,
			int backgroundcolor) {
		this(x, y, Component.literal(text), scale, color, shadow, background, backgroundcolor);
	}
	public TextElement(int x, int y, Component component, float scale, int color, boolean shadow, boolean background,
			int backgroundcolor) {
		this.component = component;
		this.x = x;
		this.y = y;
		this.color = color;
		this.backgroundcolor = backgroundcolor;
		this.scale = scale;
		this.shadow = shadow;
		this.background = background;
	}
	@Override public void renderElement(GuiGraphicsExtractor context, HudderRenderer renderer, DeltaTracker delta) {
		renderer.renderTextLine(context, component, x, y, color, scale, shadow, background, backgroundcolor);
	}
}