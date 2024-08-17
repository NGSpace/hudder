package io.github.ngspace.hudder;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.elements.AUIElement;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Hudder.java was too messy so I moved all rendering functions into this one class
 */
public class HudderRenderer {
    public static final String NL_REGEX = "\r?\n";
	public void renderFail(DrawContext context, String FailMessage) {
		String[] lines = FailMessage.split(NL_REGEX);
		int y = 1;
		for (String line : lines) {
			renderTextLine(context, line, 1, y, 0xFF5555, 1, false, true, 0xd6d6d6);
			y+=9;
		}
	}
	
	public void drawCompileResult(DrawContext context, TextRenderer renderer, CompileResult text, ConfigInfo info,
			RenderTickCounter delta) {
        int color = info.color;
        int bgcolor = info.backgroundcolor;
        boolean shadow = info.shadow;
        boolean background = info.background;
        
        //This is too complicated, imma replace it with TextElements ~~eventually~~ soon.
        
        /* Top Left */
        String[] lines = text.TopLeftText.split(NL_REGEX);
        int yoff = info.yoffset;
        int xoff = info.xoffset;
        for (String txt : lines) {
        	renderTextLine(context, txt, xoff, yoff, color, text.TLScale, shadow, background, bgcolor);
        	yoff+=info.lineHeight * text.TLScale;
        }
        
        /* Bottom Left */
        String[] BL = text.BottomLeftText.split(NL_REGEX);
        yoff = (int) (context.getScaledWindowHeight() - countLines(text.BottomLeftText) *
        		info.lineHeight * text.BLScale - info.yoffset + 1);
        xoff = info.xoffset;
        for (String txt : BL) {
        	renderTextLine(context, txt, xoff, yoff, color, text.BLScale, shadow, background, bgcolor);
        	yoff+=info.lineHeight * text.BLScale;
        }
        
        /* Top Right */
        String[] TR = text.TopRightText.split(NL_REGEX);
        yoff = info.yoffset;
//        xoff = 300 - info.xoffset;
        for (String txt : TR) {
        	xoff = (int) (context.getScaledWindowWidth() - renderer.getWidth(txt) * text.TRScale - info.xoffset);
        	renderTextLine(context, txt, xoff, yoff, color, text.TRScale, shadow, background, bgcolor);
        	yoff+=info.lineHeight * text.TRScale;
        }
        
        /* Bottom Right */
        String[] BR = text.BottomRightText.split(NL_REGEX);
        yoff = (int) (context.getScaledWindowHeight() - countLines(text.BottomRightText) *
        		info.lineHeight * text.BRScale - info.yoffset + 1);
//        xoff = 300 - info.xoffset;
        for (String txt : BR) {
        	xoff = (int) (context.getScaledWindowWidth() - renderer.getWidth(txt) * text.BRScale - info.xoffset);
        	renderTextLine(context, txt, xoff, yoff, color, text.BRScale, shadow, background, bgcolor);
        	yoff+=info.lineHeight * text.BRScale;
        }
        
        for (AUIElement e : text.elements) e.renderElement(context,delta);
    }
    public int countLines(String what) {
        int count = 1;
        for (int i = 0; i<what.length();i++) if (what.charAt(i) == '\n') count++;
        return count;
    }

	public void renderTextLine(DrawContext context, String text, int x, int y, int color, float scale, boolean shadow,
			boolean background, int backgroundColor) {
		if (background&&!"".equals(text))
			renderBlock(context,x-1f,y-1f,Hudder.ins.textRenderer.getWidth(text)+2f,9f+1f,backgroundColor);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        if (scale != 1.0f) {
            MatrixStack matrixStack = context.getMatrices();
            matrixStack.push();
            matrixStack.translate(x, y, 0);
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(-x, -y, 0);
            context.drawText(Hudder.ins.textRenderer, text, x, y, color, shadow);
            matrixStack.pop();
        } else context.drawText(Hudder.ins.textRenderer, text, x, y, color, shadow);
        RenderSystem.disableBlend();
    }
	
	public void renderBlock(DrawContext context, float x, float y, float width, float height, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bgBuilder = 
        		Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        bgBuilder.vertex(matrix, x, y+height, 0f).color(color);
        bgBuilder.vertex(matrix, x+width, y+height, 0f).color(color);
        bgBuilder.vertex(matrix, x+width, y, 0f).color(color);
        bgBuilder.vertex(matrix, x, y, 0f).color(color);
        BufferRenderer.drawWithGlobalProgram(bgBuilder.end());
        RenderSystem.disableBlend();
	}
}
