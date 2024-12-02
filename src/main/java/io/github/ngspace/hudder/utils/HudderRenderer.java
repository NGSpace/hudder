package io.github.ngspace.hudder.utils;

import java.util.function.Function;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.elements.AUIElement;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;

/**
 * Hudder.java was too messy so I moved all rendering functions into this one class
 */
public class HudderRenderer implements HudRenderCallback {
	
	private HudCompilationManager compman;
	protected static MinecraftClient mc = MinecraftClient.getInstance();
	
	
	
	public HudderRenderer(HudCompilationManager compilationManager) {
		this.compman = compilationManager;
	}
	
	
	
    public static final String NL_REGEX = "\r?\n";
	public void renderFail(DrawContext context, String FailMessage) {
		var lines = mc.textRenderer.wrapLines(StringVisitable.plain(FailMessage), mc.getWindow().getScaledWidth());
		int y = 1;
		for (OrderedText line : lines) {
        	context.drawText(mc.textRenderer, line, 1, y, 0xFF5555, true);
			y+=9;
		}
	}
	
	
	
	public void drawCompileResult(DrawContext context, TextRenderer renderer, HudInformation text, ConfigInfo info,
			RenderTickCounter delta) {
        int color = info.color;
        int bgcolor = info.backgroundcolor;
        boolean shadow = info.shadow;
        boolean background = info.background;
        
        //This is too complicated, imma replace it with TextElements ~~never~~ eventually.
        
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
        for (String txt : TR) {
        	xoff = (int) (context.getScaledWindowWidth() - renderer.getWidth(txt) * text.TRScale - info.xoffset);
        	renderTextLine(context, txt, xoff, yoff, color, text.TRScale, shadow, background, bgcolor);
        	yoff+=info.lineHeight * text.TRScale;
        }
        
        /* Bottom Right */
        String[] BR = text.BottomRightText.split(NL_REGEX);
        yoff = (int) (context.getScaledWindowHeight() - countLines(text.BottomRightText) *
        		info.lineHeight * text.BRScale - info.yoffset + 1);
        for (String txt : BR) {
        	xoff = (int) (context.getScaledWindowWidth() - renderer.getWidth(txt) * text.BRScale - info.xoffset);
        	renderTextLine(context, txt, xoff, yoff, color, text.BRScale, shadow, background, bgcolor);
        	yoff+=info.lineHeight * text.BRScale;
        }
        
        for (AUIElement e : text.elements) e.renderElement(context, this,delta);
    }
	
	
	
    public int countLines(String what) {
        int count = 1;
        for (int i = 0; i<what.length();i++) if (what.charAt(i) == '\n') count++;
        return count;
    }
    
    

	public void renderTextLine(DrawContext context, String text, int x, int y, int color, float scale, boolean shadow,
			boolean background, long backgroundColor) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (scale != 1.0f) {
            MatrixStack matrixStack = context.getMatrices();
            matrixStack.push();
            matrixStack.translate(x, y, 0);
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(-x, -y, 0);
    		if (background&&!"".equals(text))
    			renderBlock(context,x-1f,y-1f,mc.textRenderer.getWidth(text)+2f,9f+1f,backgroundColor);
            context.drawText(mc.textRenderer, text, x, y, color, shadow);
            matrixStack.pop();
        } else {
    		if (background&&!"".equals(text))
    			renderBlock(context,x-1f,y-1f,mc.textRenderer.getWidth(text)+2f,9f+1f,backgroundColor);
        	context.drawText(mc.textRenderer, text, x, y, color, shadow);
        }
        RenderSystem.disableBlend();
    }
	
	
	
	public void renderBlock(DrawContext context, float x, float y, float width, float height, long argb) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        int alpha = (int) ((argb >> 24) & 0xFF);
        int red =   (int) ((argb >> 16) & 0xFF);
        int green = (int) ((argb >>  8) & 0xFF);
        int blue =  (int) ((argb      ) & 0xFF);

        BufferBuilder bgBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS,
        		VertexFormats.POSITION_COLOR);
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        bgBuilder.vertex(matrix, x, y+height, 0f).color(red,green,blue,alpha);
        bgBuilder.vertex(matrix, x+width, y+height, 0f).color(red,green,blue,alpha);
        bgBuilder.vertex(matrix, x+width, y, 0f).color(red,green,blue,alpha);
        bgBuilder.vertex(matrix, x, y, 0f).color(red,green,blue,alpha);
        BufferRenderer.drawWithGlobalProgram(bgBuilder.end());
        RenderSystem.disableBlend();
	}
    private static final Function<Identifier, RenderLayer> hudder_gui_tr = Util.memoize(texture ->
    	RenderLayer.of("hudder_gui_tr", VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP, 1536,
    			MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, TriState.DEFAULT, false))
    			.program(RenderPhase.POSITION_TEXTURE_COLOR_PROGRAM).transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
    			.depthTest(RenderPhase.ALWAYS_DEPTH_TEST).writeMaskState(RenderPhase.COLOR_MASK).build(false)));

	public void renderTexture(DrawContext context, float[] vertices, float[] textures, Identifier id, boolean triangles) {
		context.draw(vcp -> {
	        RenderSystem.enableBlend();
	        RenderSystem.defaultBlendFunc();
	        
	        VertexConsumer vertexConsumer = vcp.getBuffer(triangles ? hudder_gui_tr.apply(id) : RenderLayer.getGuiTextured(id));
	        
	        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
	        for (int i = 0;i<vertices.length;i+=2) {
	        	vertexConsumer.vertex(matrix,vertices[i],vertices[i+1],0f).texture(textures[i],textures[i+1]).color(-1);
	        }
	        RenderSystem.disableBlend();
		});
	}
	public void renderColoredVertexArray(DrawContext context, float[] vertices, int r, int g, int b, int a, VertexFormat.DrawMode mode) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
		
        BufferBuilder vertexBuilder = Tessellator.getInstance().begin(mode,
        		VertexFormats.POSITION_COLOR);
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        for (int i = 0;i<vertices.length;i+=2)
        	vertexBuilder.vertex(matrix,vertices[i],vertices[i+1],0f).color(r, g, b, a);
        
        BufferRenderer.drawWithGlobalProgram(vertexBuilder.end());
        RenderSystem.disableBlend();
	}
	
	
	@Override
	public void onHudRender(DrawContext context, RenderTickCounter delta) {
		try {
			if (!Hudder.config.limitrate) compman.compile(delta);
			if (Hudder.config.shouldDrawResult()) {
            	RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
	            try {
	            	if (compman.result!=null)
	            		drawCompileResult(context, mc.textRenderer, compman.result, Hudder.config, delta);
	            	else
	            		renderFail(context, HudCompilationManager.LastFailMessage);
				} catch (Exception e) {
					e.printStackTrace();
				}
            	RenderSystem.disableBlend();
			}
    	} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
	}
}
