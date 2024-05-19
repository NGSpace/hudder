package io.github.ngspace.hudder.meta.elements;

import java.io.Serializable;

import net.minecraft.client.gui.DrawContext;

public abstract class Element implements Serializable {
	private static final long serialVersionUID = 6926629886320975623L;
	public abstract void renderElement(DrawContext context, float delta);
}
