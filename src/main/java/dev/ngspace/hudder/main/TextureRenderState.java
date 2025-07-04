package dev.ngspace.hudder.main;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

public record TextureRenderState(TextureSetup textureSetup, RenderPipeline pipeline, BiConsumer<VertexConsumer, Float> consumer) implements GuiElementRenderState {

	@Override
	public ScreenRectangle bounds() {
        return new ScreenRectangle(0, 0, HudderRenderer.mc.getWindow().getGuiScaledWidth(),
        		HudderRenderer.mc.getWindow().getGuiScaledHeight());
	}

	@Override
	public void buildVertices(VertexConsumer vertexConsumer, float f) {
		consumer().accept(vertexConsumer, f);
	}

	@Override
	public ScreenRectangle scissorArea() {
        return new ScreenRectangle(0, 0, HudderRenderer.mc.getWindow().getGuiScaledWidth(),
        		HudderRenderer.mc.getWindow().getGuiScaledHeight());
	}
	
}
