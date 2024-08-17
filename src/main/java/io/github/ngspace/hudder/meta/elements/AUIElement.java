package io.github.ngspace.hudder.meta.elements;

import java.io.Serializable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public abstract class AUIElement implements Serializable {
	private static final long serialVersionUID = 6926629886320975623L;
	public abstract void renderElement(DrawContext context, RenderTickCounter delta);
}
