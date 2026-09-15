package dev.ngspace.hudder.main;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;

public record TextureRenderState(TextureSetup textureSetup, RenderPipeline pipeline,
		Consumer<VertexConsumer> consumer) implements GuiElementRenderState {

	@Override
	public ScreenRectangle bounds() {
        return new ScreenRectangle(0, 0, HudderRenderer.mc.getWindow().getGuiScaledWidth(),
        		HudderRenderer.mc.getWindow().getGuiScaledHeight());
	}

	@Override
	public void buildVertices(VertexConsumer vertexConsumer) {
		consumer().accept(vertexConsumer);
	}

	@Override
	public ScreenRectangle scissorArea() {
        return new ScreenRectangle(0, 0, HudderRenderer.mc.getWindow().getGuiScaledWidth(),
        		HudderRenderer.mc.getWindow().getGuiScaledHeight());
	}
	
}
