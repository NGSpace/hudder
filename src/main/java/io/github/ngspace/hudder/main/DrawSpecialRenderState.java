package io.github.ngspace.hudder.main;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import blue.endless.jankson.annotation.Nullable;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;

public record DrawSpecialRenderState(
		BiConsumer<PoseStack, MultiBufferSource.BufferSource> drawer) implements PictureInPictureRenderState {
    @Override
    public @Nullable ScreenRectangle bounds() {
        return PictureInPictureRenderState.getBounds(x0(), y0(), x1(), y1(), null);
    }

    public static class Renderer extends PictureInPictureRenderer<DrawSpecialRenderState> {
        protected Renderer(MultiBufferSource.BufferSource vertexConsumers) {
            super(vertexConsumers);
        }

        @Override
        public Class<DrawSpecialRenderState> getRenderStateClass() {
            return DrawSpecialRenderState.class;
        }

        @Override
        protected void renderToTexture(DrawSpecialRenderState state, PoseStack matrices) {
            state.drawer().accept(matrices, bufferSource);
        }

        @Override
        protected String getTextureLabel() {
            return "DrawSpecialRenderStateRenderer";
        }
    }
    public static void register() {
        SpecialGuiElementRegistry.register(ctx -> new DrawSpecialRenderState.Renderer(ctx.vertexConsumers()));
    }
	@Override
	public float scale() {
		return 1;
	}
	@Override
	public int x0() {
		return 0;
	}
	@Override
	public int x1() {
		return HudderRenderer.mc.getWindow().getGuiScaledWidth();
	}
	@Override
	public int y0() {
		return 0;
	}
	@Override
	public int y1() {
		return HudderRenderer.mc.getWindow().getGuiScaledHeight();
	}
	@Override
	public ScreenRectangle scissorArea() {
		return null;
	}
}