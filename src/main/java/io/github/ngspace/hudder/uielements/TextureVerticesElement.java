package io.github.ngspace.hudder.uielements;

import java.io.IOException;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.main.HudderRenderer;
import io.github.ngspace.hudder.utils.HudFileUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class TextureVerticesElement extends AUIElement {

	private ResourceLocation id;
	private float[] vertices;
	private float[] textures;
	private boolean triangles;
	
	public TextureVerticesElement(String filename, float[] positionArray, float[] textureArray, boolean triangles) throws IOException,
		CompileException {
		this.id = ResourceLocation.withDefaultNamespace(filename.trim().toLowerCase());
		HudFileUtils.getAndRegisterImage(filename,id);
		this.vertices = positionArray;
		this.textures = textureArray;
		this.triangles = triangles;
		if (vertices.length!=textures.length)
			throw new CompileException("Texture array and Vertex array are not of the same length!");
	}
	
	@Override public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {
		renderer.renderTexturedVertexArray(context, vertices, textures, id, triangles);
	}
	
}
