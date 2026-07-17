package dev.ngspace.ngsmcconfig.api;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class NGSMCConfigIcon {
	
	public abstract AbstractWidget build(int x, int y, int width, int height);
	
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
		public AbstractWidget build(int x, int y, int width, int height) {
			return new NGSMCConfigSpriteIcon(x, y, width, height, sprite);
		}
		
		protected class NGSMCConfigSpriteIcon extends AbstractWidget {

			protected TextureAtlasSprite sprite;
			protected int x;
			protected int y;

			public NGSMCConfigSpriteIcon(int x, int y, int width, int height, TextureAtlasSprite sprite) {
				super(x, y, width, height, CommonComponents.EMPTY);
				this.sprite = sprite;
				this.x = x;
				this.y = y;
			}

			@Override
			protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
				graphics.blitSprite(
		                RenderPipelines.GUI_TEXTURED,
		                sprite,
		                x,
		                y,
		                width,
		                height
		        );
			}

			@Override protected void updateWidgetNarration(NarrationElementOutput output) {/* */}
			
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
		public AbstractWidget build(int x, int y, int width, int height) {
			return new NGSMCConfigTextureIcon(x, y, width, height, texture);
		}
		
		protected class NGSMCConfigTextureIcon extends AbstractWidget {

			protected Identifier texture;
			protected int x;
			protected int y;

			public NGSMCConfigTextureIcon(int x, int y, int width, int height, Identifier texture) {
				super(x, y, width, height, CommonComponents.EMPTY);
				this.texture = texture;
				this.x = x;
				this.y = y;
			}

			@Override
			protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
				graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0f, width, height, width, height);
			}

			@Override protected void updateWidgetNarration(NarrationElementOutput output) {/* */}
			
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
		public AbstractWidget build(int x, int y, int width, int height) {
			return new NGSMCConfigItemStackIcon(x, y, width, height, height/16f, stack, font);
		}
		
		@Deprecated(forRemoval = false, since = "10.1.0")
		protected class NGSMCConfigItemStackIcon extends AbstractWidget {

			@Deprecated(forRemoval = false, since = "10.1.0")
			protected ItemStack stack;
			@Deprecated(forRemoval = false, since = "10.1.0")
			protected float scale;
			@Deprecated(forRemoval = false, since = "10.1.0")
			protected Font font;
			@Deprecated(forRemoval = false, since = "10.1.0")
			protected float x;
			@Deprecated(forRemoval = false, since = "10.1.0")
			protected float y;

			@Deprecated(forRemoval = false, since = "10.1.0")
			public NGSMCConfigItemStackIcon(int x, int y, int width, int height, float scale,
					ItemStack stack, Font font) {
				super(x, y, width, height, CommonComponents.EMPTY);
				this.stack = stack;
				this.font = font;
				this.scale = scale;
				this.x = x;
				this.y = y;
			}

			@Deprecated(forRemoval = false, since = "10.1.0")
			@Override
			protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
				Matrix3x2fStack matrixStack = graphics.pose();
	            matrixStack.pushMatrix();
	            matrixStack.translate(x, y);
	            matrixStack.scale(scale, scale);
	            matrixStack.translate(-x, -y);
	            graphics.item(stack, (int)x, (int)y);
	            graphics.itemDecorations(font, stack, (int)x, (int)y);
	            matrixStack.popMatrix();
			}

			@Deprecated(forRemoval = false, since = "10.1.0")
			@Override protected void updateWidgetNarration(NarrationElementOutput output) {/* */}
			
		}
	}
}
