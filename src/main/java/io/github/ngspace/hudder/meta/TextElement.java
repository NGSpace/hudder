package io.github.ngspace.hudder.meta;

import static io.github.ngspace.hudder.Hudder.renderText;

import io.github.ngspace.hudder.config.ConfigInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

@SuppressWarnings("resource")
public class TextElement extends Element {
	public final double x1;
	public final double y1;
	public final String text;
	final float scale1;
	private ConfigInfo ci;

	public TextElement(double x1, double y1, String text, float scale, ConfigInfo info) {
		this.x1 = x1;
		this.y1 = y1;
		this.scale1 = scale;
		this.ci = info;
		this.text = text;
	}

	@Override public void RenderElement(DrawContext context) {
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		int y = (int) y1;
		int x = (int) x1;
		float scale = scale1;
		renderText(context,textRenderer, text, x, y, ci.color, scale, ci.shadow);
	}
}