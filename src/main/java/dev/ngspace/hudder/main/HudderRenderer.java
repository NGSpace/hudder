package dev.ngspace.hudder.main;

import java.util.List;

import org.joml.Matrix3x2fStack;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.uielements.AUIElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;

/**
 * Hudder.java was too messy so I moved all rendering functions into this one class
 */
public class HudderRenderer implements HudElement {
	
	private HudCompilationManager compman;
	public Identifier hudElementRegistryID = Identifier.fromNamespaceAndPath("hudder_renderer", "renderer");
	protected static Minecraft mc = Minecraft.getInstance();
    public static final String NL_REGEX = "\r?\n";
	
	
	
	public final RenderPipeline GUI_TEXTURED_TRIANGLES = RenderPipelines.register(RenderPipeline.builder(
			RenderPipelines.GUI_TEXTURED_SNIPPET).withLocation("pipeline/gui_textured_triangles")
			.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLE_STRIP).build());
	
	
	
	public HudderRenderer(HudCompilationManager compilationManager) {
		this.compman = compilationManager;
	}
	
	
	
	protected void renderFail(GuiGraphics context, String FailMessage) {
		List<FormattedCharSequence> lines = mc.font.split(FormattedText.of(FailMessage),
			mc.getWindow().getGuiScaledWidth()-10);
		int y = 1;
		for (FormattedCharSequence line : lines) {
        	context.drawString(mc.font, line, 1, y, 0xFFFF5555, true);
			y+=9;
		}
	}
	
	
	
	protected void renderHudInformation(GuiGraphics context, Font renderer, HudInformation text, HudderConfig info,
			DeltaTracker delta) {
        int color = info.color();
        int bgcolor = info.backgroundcolor();
        boolean shadow = info.shadow();
        boolean background = info.background();
        
        /* Top Left */
        String[] lines = text.TopLeftText().split(NL_REGEX);
        int yoff = info.yoffset();
        int xoff = info.xoffset();
        for (String txt : lines) {
        	renderTextLine(context, txt, xoff, yoff, color, text.TLScale(), shadow, background, bgcolor);
        	yoff+=info.lineHeight() * text.TLScale();
        }
        
        /* Bottom Left */
        String[] BL = text.BottomLeftText().split(NL_REGEX);
        yoff = (int) (context.guiHeight() - countLines(text.BottomLeftText()) *
        		info.lineHeight() * text.BLScale() - info.yoffset() + 1);
        xoff = info.xoffset();
        for (String txt : BL) {
        	renderTextLine(context, txt, xoff, yoff, color, text.BLScale(), shadow, background, bgcolor);
        	yoff+=info.lineHeight() * text.BLScale();
        }
        
        /* Top Right */
        String[] TR = text.TopRightText().split(NL_REGEX);
        yoff = info.yoffset();
        for (String txt : TR) {
        	xoff = (int) (context.guiWidth() - renderer.width(txt) * text.TRScale() - info.xoffset());
        	renderTextLine(context, txt, xoff, yoff, color, text.TRScale(), shadow, background, bgcolor);
        	yoff+=info.lineHeight() * text.TRScale();
        }
        
        /* Bottom Right */
        String[] BR = text.BottomRightText().split(NL_REGEX);
        yoff = (int) (context.guiHeight() - countLines(text.BottomRightText()) *
        		info.lineHeight() * text.BRScale() - info.yoffset() + 1);
        for (String txt : BR) {
        	xoff = (int) (context.guiWidth() - renderer.width(txt) * text.BRScale() - info.xoffset());
        	renderTextLine(context, txt, xoff, yoff, color, text.BRScale(), shadow, background, bgcolor);
        	yoff+=info.lineHeight() * text.BRScale();
        }
        
        for (AUIElement e : text.elements()) e.renderElement(context, this, delta);
    }
	
	
	
    private int countLines(String str) {
        int count = 1;
        for (int i = 0; i<str.length();i++) if (str.charAt(i) == '\n') count++;
        return count;
    }
    
    

	public void renderTextLine(GuiGraphics context, String text, int x, int y, int color, float scale, boolean shadow,
			boolean background, int backgroundColor) {
        if (scale != 1.0f) {
            Matrix3x2fStack matrixStack = context.pose();
            matrixStack.pushMatrix();
            matrixStack.translate(x, y);
            matrixStack.scale(scale, scale);
            matrixStack.translate(-x, -y);
    		if (background&&!"".equals(text))
    			renderBlock(context,x-1,y-1,mc.font.width(text)+2,10,backgroundColor);
            context.drawString(mc.font, text, x, y, color, shadow);
            matrixStack.popMatrix();
        } else {
    		if (background&&!"".equals(text))
    			renderBlock(context,x-1,y-1,mc.font.width(text)+2,10,backgroundColor);
        	context.drawString(mc.font, text, x, y, color, shadow);
        }
    }
	
	
	
	public void renderBlock(GuiGraphics graphics, int x, int y, int width, int height, int argb) {
		graphics.fill(x, y, x+width, y+height, argb);
	}
	
	
	
	public void renderTexture9Slice(GuiGraphics context, Identifier id, float x, float y, float width,
			float height, float[] slices) {
		var tex = mc.getTextureManager().getTexture(id);
		context.guiRenderState.submitGuiElement(new TextureRenderState(TextureSetup.singleTexture(tex.getTextureView(),
				tex.getSampler()), RenderPipelines.GUI_TEXTURED, vconsumer->{
		    Matrix3x2fStack matrix = context.pose();
	        NativeImage img = ((DynamicTexture)mc.getTextureManager().getTexture(id)).getPixels();
	        int texwidth = img.getWidth();
	        int texheight = img.getHeight();

	        float left = Math.min(slices[0],texwidth/2f);
	        float right = Math.min(slices[1],texwidth/2f);
	        float top = Math.min(slices[2],texheight/2f);
	        float bottom = Math.min(slices[3],texheight/2f);
	        
	        float middlestart_hor = x+left;
	        float middleend_hor = x+width-right;
	        float middleend_tex_hor = (texwidth-right)/texwidth;
	        float tls = left/texwidth;
	        
	        
	        float middlestart_ver = y+top;
	        float middleend_ver = y + height - bottom;
	        float middleend_tex_ver = (texheight-bottom)/texheight;
	        float lts = top/texheight;
	        
	        // Top-left
	        vconsumer.addVertexWith2DPose(matrix,x,y).setUv(0,0).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x,y+top).setUv(0,lts).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x+left,y+top).setUv(tls,lts).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x+left,y).setUv(tls,0).setColor(-1);
	        
	        // Top-middle
	        vconsumer.addVertexWith2DPose(matrix,middlestart_hor,y).setUv(tls, 0).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,middlestart_hor,y+top).setUv(tls,lts).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,middleend_hor,y+top).setUv(middleend_tex_hor,lts).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,middleend_hor,y).setUv(middleend_tex_hor,0).setColor(-1);
	        
	        // Top-right
	        vconsumer.addVertexWith2DPose(matrix,middleend_hor,y).setUv(middleend_tex_hor,0).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,middleend_hor,y+top).setUv(middleend_tex_hor,lts).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x+width,y+top).setUv(1,lts).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x+width,y).setUv(1,0).setColor(-1);
	        
	        
	        
	        // Middle-left
	        vconsumer.addVertexWith2DPose(matrix,x,middlestart_ver).setUv(0,lts).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x,middleend_ver).setUv(0,middleend_tex_ver).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x+left,middleend_ver).setUv(tls,middleend_tex_ver).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x+left,middlestart_ver).setUv(tls,lts).setColor(-1);
	        
	        // Middle-middle
	        vconsumer.addVertexWith2DPose(matrix,middlestart_hor,middlestart_ver).setUv(tls, lts).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,middlestart_hor,middleend_ver).setUv(tls,middleend_tex_ver).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,middleend_hor,middleend_ver).setUv(middleend_tex_hor,middleend_tex_ver).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,middleend_hor,middlestart_ver).setUv(middleend_tex_hor,lts).setColor(-1);
	        
	        // Middle-right
	        vconsumer.addVertexWith2DPose(matrix,middleend_hor,middlestart_ver).setUv(middleend_tex_hor,lts).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,middleend_hor,middleend_ver).setUv(middleend_tex_hor,middleend_tex_ver).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x+width,middleend_ver).setUv(1,middleend_tex_ver).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x+width,middlestart_ver).setUv(1,lts).setColor(-1);
	        
	        
	        
	        // Bottom-left
	        vconsumer.addVertexWith2DPose(matrix,x,middleend_ver).setUv(0,middleend_tex_ver).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x,y+height).setUv(0,1).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x+left,y+height).setUv(tls,1).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x+left,middleend_ver).setUv(tls,middleend_tex_ver).setColor(-1);
	        
	        // Bottom-middle
	        vconsumer.addVertexWith2DPose(matrix,middlestart_hor,middleend_ver).setUv(tls, middleend_tex_ver).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,middlestart_hor,y+height).setUv(tls,1).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,middleend_hor,y+height).setUv(middleend_tex_hor,1).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,middleend_hor,middleend_ver).setUv(middleend_tex_hor,middleend_tex_ver).setColor(-1);
	        
	        // Bottom-right
	        vconsumer.addVertexWith2DPose(matrix,middleend_hor,middleend_ver).setUv(middleend_tex_hor,middleend_tex_ver).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,middleend_hor,y+height).setUv(middleend_tex_hor,1).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x+width,y+height).setUv(1,1).setColor(-1);
	        vconsumer.addVertexWith2DPose(matrix,x+width,middleend_ver).setUv(1,middleend_tex_ver).setColor(-1);
		}));
	}

	public void renderTexturedVertexArray(GuiGraphics context, float[] vertices, float[] textures,
			Identifier id, boolean triangles) {
		var tex = mc.getTextureManager().getTexture(id);
		context.guiRenderState.submitGuiElement(new TextureRenderState(TextureSetup.singleTexture(tex.getTextureView(),
				tex.getSampler()),
				triangles ? GUI_TEXTURED_TRIANGLES : RenderPipelines.GUI_TEXTURED, vconsumer->{
		        Matrix3x2fStack matrix = context.pose();
		        
	        for (int i = 0;i<vertices.length;i+=2) {
	        	vconsumer.addVertexWith2DPose(matrix,vertices[i],vertices[i+1]).setUv(textures[i],textures[i+1]).setColor(-1);
	        }
		}));
	}
	
	
	
	/**
	 * Draws the provided vertices on screen with the provided color and render mode
	 * 
	 * @deprecated Use {@link #renderColoredVertexArray(GuiGraphics, float[], int, boolean)} instead.
	 * If needed, use {@link ARGB#color(int, int, int, int)}.
	 * 
	 * @param context The render context
	 * @param vertices The array holding all the vertices
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param a alpha
	 * @param mode The rendering mode
	 */
	@Deprecated(since = "TBD", forRemoval = false)
	public void renderColoredVertexArray(GuiGraphics context, float[] vertices, int r, int g, int b, int a,
			boolean triangle_strip) {
		renderColoredVertexArray(context, vertices, ARGB.color(r, g, b, a), triangle_strip);
	}
	
	
	
	/**
	 * Draws the provided vertices on screen with the provided color and render mode
	 * @param context The render context
	 * @param vertices The array holding all the vertices
	 * @param argb The ARGB color value
	 * @param mode The rendering mode
	 */
	public void renderColoredVertexArray(GuiGraphics context, float[] vertices, int argb, boolean triangle_strip) {
		context.guiRenderState.submitGuiElement(new TextureRenderState(TextureSetup.noTexture(),
			triangle_strip ? GUI_TEXTURED_TRIANGLES : RenderPipelines.GUI_TEXTURED, vconsumer -> {
			
	        Matrix3x2fStack matrix = context.pose();
	        
	        for (int i = 0;i<vertices.length;i+=2)
	        	vconsumer.addVertexWith2DPose(matrix,vertices[i],vertices[i+1]).setColor(argb).setUv(0, 0);
		}));
	}
	
	
	
	@Override public void render(GuiGraphics context, DeltaTracker delta) {
		try {
			if (!Hudder.config.limitrate()) compman.compile(delta);
			if (Hudder.config.shouldDrawResult()) {
	            try {
	            	if (compman.getResult()!=null)
	            		renderHudInformation(context, mc.font, compman.getResult(), Hudder.config, delta);
	            	else
	            		renderFail(context, HudCompilationManager.LastFailMessage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
    	} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
	}
}
