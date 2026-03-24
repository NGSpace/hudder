package dev.ngspace.hudder.uielements;

import dev.ngspace.hudder.main.HudderRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class BuiltInTextureElement extends AUIElement {

	private static final Minecraft mc = Minecraft.getInstance();

	private final Identifier atlasId;
	private final Identifier spriteId;
	private final int x;
	private final int y;
	private final int width;
	private final int height;

	public BuiltInTextureElement(Identifier atlasId, Identifier spriteId, int x, int y, int width, int height) {
		this.atlasId = atlasId;
		this.spriteId = spriteId;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public void renderElement(GuiGraphicsExtractor context, HudderRenderer renderer, DeltaTracker delta) {

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