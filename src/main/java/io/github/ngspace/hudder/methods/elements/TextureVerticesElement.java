package io.github.ngspace.hudder.methods.elements;

import java.io.IOException;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.utils.HudFileUtils;
import io.github.ngspace.hudder.utils.HudderRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class TextureVerticesElement extends AUIElement {

	private static final long serialVersionUID = -4934891927885170626L;
	private Identifier id;
	private float[] vertices;
	private float[] textures;
	private boolean triangles;
	
	public TextureVerticesElement(String filename, float[] positionArray, float[] textureArray, boolean triangles) throws IOException,
		CompileException {
		this.id = Identifier.of(filename.trim().toLowerCase());
		HudFileUtils.getAndRegisterImage(filename,id);
		this.vertices = positionArray;
		this.textures = textureArray;
		this.triangles = triangles;
		if (vertices.length!=textures.length)
			throw new CompileException("Texture array and Vertex array are not of the same length!");
	}
	
	@Override public void renderElement(DrawContext context, HudderRenderer renderer, RenderTickCounter delta) {
		renderer.renderTexture(context, vertices, textures, id, triangles);
	}
	
}
