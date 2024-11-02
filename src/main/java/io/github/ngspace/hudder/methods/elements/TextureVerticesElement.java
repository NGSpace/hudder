package io.github.ngspace.hudder.methods.elements;

import java.io.IOException;

import io.github.ngspace.hudder.util.HudFileUtils;
import io.github.ngspace.hudder.util.HudderRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class TextureVerticesElement extends AUIElement {

	private static final long serialVersionUID = -4934891927885170626L;
	private Identifier id;
	private float[] pos;
	private float[] tex;
	
	public TextureVerticesElement(String filename, float[] positionArray, float[] textureArray) throws IOException {
		this.id = Identifier.of(filename.trim().toLowerCase());
		HudFileUtils.getAndRegisterImage(filename,id);
		this.pos = positionArray;
		this.tex = textureArray;
	}
	
	@Override public void renderElement(DrawContext context, HudderRenderer renderer, RenderTickCounter delta) {
		renderer.renderTexture(context, pos, tex, id);
	}
	
}
