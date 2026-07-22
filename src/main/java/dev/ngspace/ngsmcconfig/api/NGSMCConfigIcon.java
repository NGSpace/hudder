package dev.ngspace.ngsmcconfig.api;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class NGSMCConfigIcon {


	public abstract void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a,
			int width, int height, int x, int y);
	
	/**
	 * Uses GuiGraphicsExtractor.blitSprite(... TextureAtlasSprite sprite ...) to draw the given sprite on screen
	 */
	public static class SpriteIcon extends NGSMCConfigIcon {
		
		private TextureAtlasSprite sprite;

		public SpriteIcon(TextureAtlasSprite sprite) {
			this.sprite = sprite;
		}

		public SpriteIcon(String atlas, String sprite) {
			this.sprite = Minecraft.getInstance()
					.getAtlasManager()
					.getAtlasOrThrow(Identifier.parse(atlas))
					.getSprite(Identifier.parse(sprite));
		}

		@Override
		public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, int width,
				int height, int x, int y) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height);
		}
	}
	
	
	/**
	 * Uses GuiGraphicsExtractor.blit(... Identifier texture ...) to draw the texture attached to the given
	 * Identifier on screen.
	 */
	public static class TextureIcon extends NGSMCConfigIcon {
		
		private Identifier texture;

		public TextureIcon(Identifier texture) {
			this.texture = texture;
		}

		@Override
		public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, int width,
				int height, int x, int y) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0f, width, height, width, height);
		}
	}
	
	
	/**
	 * Uses GuiGraphicsExtractor.item(... ItemStack stack ...) to draw an ItemStack on screen and uses matrix
	 * stack fuckery to scale it to fit the given height and width
	 * 
	 * @deprecated It's just fucking broken. If constructed before starting a game it will crash the game.
	 */
	@Deprecated(forRemoval = false, since = "10.1.0")
	public static class ItemStackIcon extends NGSMCConfigIcon {
		
		private ItemStack stack;
		private Font font;

		@Deprecated(forRemoval = false, since = "10.1.0")
		public ItemStackIcon(ItemStack stack) {
			this(stack, Minecraft.getInstance().font);
		}

		@Deprecated(forRemoval = false, since = "10.1.0")
		public ItemStackIcon(ItemStack stack, Font font) {
			this.stack = stack;
			this.font = font;
		}

		@Deprecated(forRemoval = false, since = "10.1.0")
		public ItemStackIcon(Item item) {
			this(item, Minecraft.getInstance().font);
		}

		@Deprecated(forRemoval = false, since = "10.1.0")
		public ItemStackIcon(Item item, Font font) {
			var itemholder = BuiltInRegistries.ITEM.wrapAsHolder(item);
			
			this.stack = new ItemStack(itemholder, 1, DataComponentPatch.EMPTY);
			this.font = font;
		}

		@Deprecated(forRemoval = false, since = "10.1.0")
		@Override
		public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, int width,
				int height, int x, int y) {
			Matrix3x2fStack matrixStack = graphics.pose();
            matrixStack.pushMatrix();
            matrixStack.translate(x, y);
            matrixStack.scale(height/16f, height/16f);
            matrixStack.translate(-x, -y);
            graphics.item(stack, x, y);
            graphics.itemDecorations(font, stack, x, y);
            matrixStack.popMatrix();
		}
	}
}
