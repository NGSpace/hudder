package io.github.ngspace.hudder.uielements;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.main.HudderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderPipelines;

@Environment(EnvType.CLIENT)
public class BuiltInTextureElement extends AUIElement {

	private static final Minecraft mc = Minecraft.getInstance();

	private final Identifier atlasId;
	private final Identifier spriteId;
	private final int x, y;
	private final int width, height;

	public BuiltInTextureElement(Identifier atlasId, Identifier spriteId, int x, int y, int width, int height) {
		this.atlasId = atlasId;
		this.spriteId = spriteId;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public void renderElement(GuiGraphics context, HudderRenderer renderer, DeltaTracker delta) {

		TextureAtlasSprite sprite = mc.getAtlasManager().getAtlasOrThrow(atlasId).getSprite(spriteId);

        context.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                sprite,
                x,
                y,
                width,
                height
        );
    }
}