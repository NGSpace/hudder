package io.github.ngspace.hudder.hudder;

import java.util.function.Consumer;
import java.util.function.Function;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.elements.AUIElement;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.TriState;

/**
 * Hudder.java was too messy so I moved all rendering functions into this one class
 */
public class HudderRenderer implements HudRenderCallback {
	
	private HudCompilationManager compman;
	protected static Minecraft mc = Minecraft.getInstance();
	
	
	
	public HudderRenderer(HudCompilationManager compilationManager) {
		this.compman = compilationManager;
	}
	
	
	
    public static final String NL_REGEX = "\r?\n";
	public void renderFail(GuiGraphics context, String FailMessage) {
		var lines = mc.font.split(FormattedText.of(FailMessage), mc.getWindow().getGuiScaledWidth());
		int y = 1;
		for (FormattedCharSequence line : lines) {
        	context.drawString(mc.font, line, 1, y, 0xFF5555, true);
			y+=9;
		}
	}
	
	
	
	public void drawCompileResult(GuiGraphics context, Font renderer, HudInformation text, ConfigInfo info,
			DeltaTracker delta) {
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
        yoff = (int) (context.guiHeight() - countLines(text.BottomLeftText) *
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
        	xoff = (int) (context.guiWidth() - renderer.width(txt) * text.TRScale - info.xoffset);
        	renderTextLine(context, txt, xoff, yoff, color, text.TRScale, shadow, background, bgcolor);
        	yoff+=info.lineHeight * text.TRScale;
        }
        
        /* Bottom Right */
        String[] BR = text.BottomRightText.split(NL_REGEX);
        yoff = (int) (context.guiHeight() - countLines(text.BottomRightText) *
        		info.lineHeight * text.BRScale - info.yoffset + 1);
        for (String txt : BR) {
        	xoff = (int) (context.guiWidth() - renderer.width(txt) * text.BRScale - info.xoffset);
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
    
    

	public void renderTextLine(GuiGraphics context, String text, int x, int y, int color, float scale, boolean shadow,
			boolean background, long backgroundColor) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (scale != 1.0f) {
            PoseStack matrixStack = context.pose();
            matrixStack.pushPose();
            matrixStack.translate(x, y, 0);
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(-x, -y, 0);
    		if (background&&!"".equals(text))
    			renderBlock(context,x-1f,y-1f,mc.font.width(text)+2f,9f+1f,backgroundColor);
            context.drawString(mc.font, text, x, y, color, shadow);
            matrixStack.popPose();
        } else {
    		if (background&&!"".equals(text))
    			renderBlock(context,x-1f,y-1f,mc.font.width(text)+2f,9f+1f,backgroundColor);
        	context.drawString(mc.font, text, x, y, color, shadow);
        }
        RenderSystem.disableBlend();
    }
	
	
	
	public void renderBlock(GuiGraphics context, float x, float y, float width, float height, long argb) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(CoreShaders.POSITION_COLOR);
        int alpha = (int) ((argb >> 24) & 0xFF);
        int red =   (int) ((argb >> 16) & 0xFF);
        int green = (int) ((argb >>  8) & 0xFF);
        int blue =  (int) ((argb      ) & 0xFF);

        BufferBuilder bgBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,
        		DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix = context.pose().last().pose();
        bgBuilder.addVertex(matrix, x, y+height, 0f).setColor(red,green,blue,alpha);
        bgBuilder.addVertex(matrix, x+width, y+height, 0f).setColor(red,green,blue,alpha);
        bgBuilder.addVertex(matrix, x+width, y, 0f).setColor(red,green,blue,alpha);
        bgBuilder.addVertex(matrix, x, y, 0f).setColor(red,green,blue,alpha);
        BufferUploader.drawWithShader(bgBuilder.build());
        RenderSystem.disableBlend();
	}
    private static final Function<ResourceLocation, RenderType> hudder_gui_tr = Util.memoize(texture ->
    RenderType.create("hudder_gui_tr", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLE_STRIP, 786432,
    		RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(texture,
    		TriState.FALSE, false)).setShaderState(RenderStateShard.POSITION_TEXTURE_COLOR_SHADER).setTransparencyState
    		(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
    		.createCompositeState(false)));

	public void renderTexture(GuiGraphics context, float[] vertices, float[] textures, ResourceLocation id, boolean triangles) {
		context.drawSpecial((Consumer<MultiBufferSource>)(vcp -> {
	        RenderSystem.enableBlend();
	        RenderSystem.defaultBlendFunc();
	        
	        VertexConsumer vertexConsumer = vcp.getBuffer(triangles ? hudder_gui_tr.apply(id) : RenderType.guiTextured(id));
	        
	        Matrix4f matrix = context.pose().last().pose();
	        for (int i = 0;i<vertices.length;i+=2) {
	        	vertexConsumer.addVertex(matrix,vertices[i],vertices[i+1],0f).setUv(textures[i],textures[i+1]).setColor(-1);
	        }
	        RenderSystem.disableBlend();
		}));
	}
	public void renderColoredVertexArray(GuiGraphics context, float[] vertices, int r, int g, int b, int a, VertexFormat.Mode mode) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(CoreShaders.POSITION_COLOR);
		
        BufferBuilder vertexBuilder = Tesselator.getInstance().begin(mode,
        		DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix = context.pose().last().pose();

        for (int i = 0;i<vertices.length;i+=2)
        	vertexBuilder.addVertex(matrix,vertices[i],vertices[i+1],0f).setColor(r, g, b, a);
        
        BufferUploader.drawWithShader(vertexBuilder.build());
        RenderSystem.disableBlend();
	}
	
	
	@Override
	public void onHudRender(GuiGraphics context, DeltaTracker delta) {
		try {
			if (!Hudder.config.limitrate) compman.compile(delta);
			if (Hudder.config.shouldDrawResult()) {
            	RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
	            try {
	            	if (compman.result!=null)
	            		drawCompileResult(context, mc.font, compman.result, Hudder.config, delta);
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
