package dev.ngspace.hudder.uielements.primitives;

import dev.ngspace.hudder.main.HudderRenderer;
import dev.ngspace.hudder.uielements.AUIElement;
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
	public final float rotation;

	/**
	 * @deprecated use {@link #TextElement(int, int, Component, float, int, boolean, boolean, int, float)}
	 */
	@Deprecated(since = "10.1.0", forRemoval = true)
	public TextElement(int x, int y, String text, float scale, int color, boolean shadow, boolean background,
			int backgroundcolor) {
		this(x, y, Component.literal(text), scale, color, shadow, background, backgroundcolor);
	}
	/**
	 * @deprecated use {@link #TextElement(int, int, Component, float, int, boolean, boolean, int, float)}
	 */
	@Deprecated(since = "10.3.0", forRemoval = true)
	public TextElement(int x, int y, Component component, float scale, int color, boolean shadow, boolean background,
			int backgroundcolor) {
		this(x, y, component, scale, color, shadow, background, backgroundcolor, 0);
	}
	public TextElement(int x, int y, Component component, float scale, int color, boolean shadow, boolean background,
			int backgroundcolor, float rotation) {
		this.component = component;
		this.x = x;
		this.y = y;
		this.color = color;
		this.backgroundcolor = backgroundcolor;
		this.scale = scale;
		this.shadow = shadow;
		this.background = background;
		this.rotation = rotation;
	}
	@Override public void renderElement(GuiGraphicsExtractor context, HudderRenderer renderer,
			DeltaTracker delta) {
		renderer.renderTextLine(context, component, x, y, color, scale, shadow, background, backgroundcolor, rotation);
	}
}