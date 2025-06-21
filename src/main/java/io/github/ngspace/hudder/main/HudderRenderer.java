package io.github.ngspace.hudder.main;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.main.config.HudderConfig;
import io.github.ngspace.hudder.uielements.AUIElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

/**
 * Hudder.java was too messy so I moved all rendering functions into this one class
 */
public class HudderRenderer implements HudElement {
	
	private HudCompilationManager compman;
	public ResourceLocation hudElementRegistryID = ResourceLocation.fromNamespaceAndPath("hudder_renderer", "herID");
	protected static Minecraft mc = Minecraft.getInstance();
    public static final String NL_REGEX = "\r?\n";
	public static final ResourceLocation RENDER_LAYER = ResourceLocation.fromNamespaceAndPath("hudder", "hudder_render_layer");
    
//    public final RenderType TriangleColorRenderType;
//    public final Function<ResourceLocation, RenderType> TriangleTextureRenderType;
	
	public HudderRenderer(HudCompilationManager compilationManager) {
		this.compman = compilationManager;
//		
//		// RenderType.gui() but TRIANGLE_STRIP
//		TriangleColorRenderType = RenderType.create("gui", 786432, RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
//				.withLocation("pipeline/gui").withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode
//				.TRIANGLE_STRIP).build(), RenderType.CompositeState.builder().createCompositeState(false));
//		// RenderType.gui() but TRIANGLE_STRIP
//		ColorRenderType = RenderType.create("gui", 786432, RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
//				.withLocation("pipeline/gui").withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode
//				.TRIANGLE_STRIP).build(), RenderType.CompositeState.builder().createCompositeState(false));
//		
//		// RenderType.guiTextured(...) but TRIANGLE_STRIP
//		TriangleTextureRenderType = Util.memoize(id -> RenderType.create("gui_textured", 786432, RenderPipeline
//				.builder(RenderPipelines.GUI_TEXTURED_SNIPPET).withLocation("pipeline/gui_textured")
//				.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP).build(),
//				RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(id,
//				TriState.FALSE, false)).createCompositeState(false)));
	}
	
	
	
	public void renderFail(GuiGraphics context, String FailMessage) {
		var lines = mc.font.split(FormattedText.of(FailMessage), mc.getWindow().getGuiScaledWidth());
		int y = 1;
		for (FormattedCharSequence line : lines) {
        	context.drawString(mc.font, line, 1, y, 0xFF5555, true);
			y+=9;
		}
	}
	
	
	
	public void drawCompileResult(GuiGraphics context, Font renderer, HudInformation text, HudderConfig info,
			DeltaTracker delta) {
        int color = info.color;
        int bgcolor = info.backgroundcolor;
        boolean shadow = info.shadow;
        boolean background = info.background;
        
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
        
        for (AUIElement e : text.elements) e.renderElement(context, this, delta);
    }
	
	
	
    public int countLines(String str) {
        int count = 1;
        for (int i = 0; i<str.length();i++) if (str.charAt(i) == '\n') count++;
        return count;
    }
    
    

	public void renderTextLine(GuiGraphics context, String text, int x, int y, int color, float scale, boolean shadow,
			boolean background, long backgroundColor) {
//        if (scale != 1.0f) {
//            PoseStack matrixStack = context.pose();
//            matrixStack.pushPose();
//            matrixStack.translate(x, y, 0);
//            matrixStack.scale(scale, scale, scale);
//            matrixStack.translate(-x, -y, 0);
//    		if (background&&!"".equals(text))
//    			renderBlock(context,x-1f,y-1f,mc.font.width(text)+2f,9f+1f,backgroundColor);
//            context.drawString(mc.font, text, x, y, color, shadow);
//            matrixStack.popPose();
//        } else {
    		if (background&&!"".equals(text))
    			renderBlock(context,x-1f,y-1f,mc.font.width(text)+2f,9f+1f,backgroundColor);
        	context.drawString(mc.font, text, x, y, color, shadow);
//        }
    }
	
	
	
	public void renderBlock(GuiGraphics context, float x, float y, float width, float height, long argb) {
		int alpha = (int) ((argb >> 24) & 0xFF);
		int red =   (int) ((argb >> 16) & 0xFF);
		int green = (int) ((argb >>  8) & 0xFF);
		int blue =  (int) ((argb      ) & 0xFF);
//		context.drawSpecial(v->{
//	        VertexConsumer vconsumer = v.getBuffer(RenderType.gui());
//	        Matrix4f matrix = context.pose().last().pose();
//	        
//	        vconsumer.addVertex(matrix, x, y+height, 0f).setColor(red,green,blue,alpha).setUv2(green, blue);
//	        vconsumer.addVertex(matrix, x+width, y+height, 0f).setColor(red,green,blue,alpha);
//	        vconsumer.addVertex(matrix, x+width, y, 0f).setColor(red,green,blue,alpha);
//	        vconsumer.addVertex(matrix, x, y, 0f).setColor(red,green,blue,alpha);
//		});
	}
	
	public void renderTexture9Slice(GuiGraphics context, ResourceLocation id, float x, float y, float width,
			float height, float[] slices) {
//		context.drawSpecial((Consumer<MultiBufferSource>)(vcp -> {
//	        VertexConsumer vconsumer = vcp.getBuffer(RenderType.guiTextured(id));
//	        
//	        Matrix4f matrix = context.pose().last().pose();
//	        NativeImage img = ((DynamicTexture)mc.getTextureManager().getTexture(id)).getPixels();
//	        int texwidth = img.getWidth();
//	        int texheight = img.getHeight();
//
//	        float left = Math.min(slices[0],texwidth/2f);
//	        float right = Math.min(slices[1],texwidth/2f);
//	        float top = Math.min(slices[2],texheight/2f);
//	        float bottom = Math.min(slices[3],texheight/2f);
//	        
//	        float middlestart_hor = x+left;
//	        float middleend_hor = x+width-right;
//	        float middleend_tex_hor = (texwidth-right)/texwidth;
//	        float tls = left/texwidth;
//	        
//	        
//	        float middlestart_ver = y+top;
//	        float middleend_ver = y + height - bottom;
//	        float middleend_tex_ver = (texheight-bottom)/texheight;
//	        float lts = top/texheight;
//	        
//	        // Top-left
//	        vconsumer.addVertex(matrix,x,y,0f).setUv(0,0).setColor(-1);
//	        vconsumer.addVertex(matrix,x,y+top,0f).setUv(0,lts).setColor(-1);
//	        vconsumer.addVertex(matrix,x+left,y+top,0f).setUv(tls,lts).setColor(-1);
//	        vconsumer.addVertex(matrix,x+left,y,0f).setUv(tls,0).setColor(-1);
//	        
//	        // Top-middle
//	        vconsumer.addVertex(matrix,middlestart_hor,y, 0f).setUv(tls, 0).setColor(-1);
//	        vconsumer.addVertex(matrix,middlestart_hor,y+top,0f).setUv(tls,lts).setColor(-1);
//	        vconsumer.addVertex(matrix,middleend_hor,y+top,0f).setUv(middleend_tex_hor,lts).setColor(-1);
//	        vconsumer.addVertex(matrix,middleend_hor,y,0f).setUv(middleend_tex_hor,0).setColor(-1);
//	        
//	        // Top-right
//	        vconsumer.addVertex(matrix,middleend_hor,y,0f).setUv(middleend_tex_hor,0).setColor(-1);
//	        vconsumer.addVertex(matrix,middleend_hor,y+top,0f).setUv(middleend_tex_hor,lts).setColor(-1);
//	        vconsumer.addVertex(matrix,x+width,y+top,0f).setUv(1,lts).setColor(-1);
//	        vconsumer.addVertex(matrix,x+width,y,0f).setUv(1,0).setColor(-1);
//	        
//	        
//	        
//	        // Middle-left
//	        vconsumer.addVertex(matrix,x,middlestart_ver,0f).setUv(0,lts).setColor(-1);
//	        vconsumer.addVertex(matrix,x,middleend_ver,0f).setUv(0,middleend_tex_ver).setColor(-1);
//	        vconsumer.addVertex(matrix,x+left,middleend_ver,0f).setUv(tls,middleend_tex_ver).setColor(-1);
//	        vconsumer.addVertex(matrix,x+left,middlestart_ver,0f).setUv(tls,lts).setColor(-1);
//	        
//	        // Middle-middle
//	        vconsumer.addVertex(matrix,middlestart_hor,middlestart_ver, 0f).setUv(tls, lts).setColor(-1);
//	        vconsumer.addVertex(matrix,middlestart_hor,middleend_ver,0f).setUv(tls,middleend_tex_ver).setColor(-1);
//	        vconsumer.addVertex(matrix,middleend_hor,middleend_ver,0f).setUv(middleend_tex_hor,middleend_tex_ver).setColor(-1);
//	        vconsumer.addVertex(matrix,middleend_hor,middlestart_ver,0f).setUv(middleend_tex_hor,lts).setColor(-1);
//	        
//	        // Middle-right
//	        vconsumer.addVertex(matrix,middleend_hor,middlestart_ver,0f).setUv(middleend_tex_hor,lts).setColor(-1);
//	        vconsumer.addVertex(matrix,middleend_hor,middleend_ver,0f).setUv(middleend_tex_hor,middleend_tex_ver).setColor(-1);
//	        vconsumer.addVertex(matrix,x+width,middleend_ver,0f).setUv(1,middleend_tex_ver).setColor(-1);
//	        vconsumer.addVertex(matrix,x+width,middlestart_ver,0f).setUv(1,lts).setColor(-1);
//	        
//	        
//	        
//	        // Bottom-left
//	        vconsumer.addVertex(matrix,x,middleend_ver,0f).setUv(0,middleend_tex_ver).setColor(-1);
//	        vconsumer.addVertex(matrix,x,y+height,0f).setUv(0,1).setColor(-1);
//	        vconsumer.addVertex(matrix,x+left,y+height,0f).setUv(tls,1).setColor(-1);
//	        vconsumer.addVertex(matrix,x+left,middleend_ver,0f).setUv(tls,middleend_tex_ver).setColor(-1);
//	        
//	        // Bottom-middle
//	        vconsumer.addVertex(matrix,middlestart_hor,middleend_ver, 0f).setUv(tls, middleend_tex_ver).setColor(-1);
//	        vconsumer.addVertex(matrix,middlestart_hor,y+height,0f).setUv(tls,1).setColor(-1);
//	        vconsumer.addVertex(matrix,middleend_hor,y+height,0f).setUv(middleend_tex_hor,1).setColor(-1);
//	        vconsumer.addVertex(matrix,middleend_hor,middleend_ver,0f).setUv(middleend_tex_hor,middleend_tex_ver).setColor(-1);
//	        
//	        // Bottom-right
//	        vconsumer.addVertex(matrix,middleend_hor,middleend_ver,0f).setUv(middleend_tex_hor,middleend_tex_ver).setColor(-1);
//	        vconsumer.addVertex(matrix,middleend_hor,y+height,0f).setUv(middleend_tex_hor,1).setColor(-1);
//	        vconsumer.addVertex(matrix,x+width,y+height,0f).setUv(1,1).setColor(-1);
//	        vconsumer.addVertex(matrix,x+width,middleend_ver,0f).setUv(1,middleend_tex_ver).setColor(-1);
//		}));
	}

	public void renderTexturedVertexArray(GuiGraphics context, float[] vertices, float[] textures,
			ResourceLocation id, boolean triangles) {
//		context.drawSpecial((Consumer<MultiBufferSource>)(vcp -> {
//	        VertexConsumer vertexConsumer =
//	        		vcp.getBuffer(triangles ? TriangleTextureRenderType.apply(id) : RenderType.guiTextured(id));
//	        
//	        var matrix = context.pose();
//	        for (int i = 0;i<vertices.length;i+=2) {
//	        	vertexConsumer.addVertexWith2DPose(matrix,vertices[i],vertices[i+1],0f).setUv(textures[i],textures[i+1]).setColor(-1);
//	        }
//		}));
	}
	
	
	
	/**
	 * Draws the provided vertices on screen with the provided color and render mode
	 * @param context The render context
	 * @param vertices The array holding all the vertices
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param a alpha
	 * @param mode The rendering mode
	 */
	public void renderColoredVertexArray(GuiGraphics context, float[] vertices, int r, int g, int b, int a,
			boolean triangle_strip) {
//		context.guiRenderState.submitPicturesInPictureState(new DrawSpecialRenderState((v, f)->{
//	        VertexConsumer vconsumer = f.getBuffer(RenderPipelines.GUI);
//	        Matrix4f matrix = context.pose().last().pose();
//
//	        for (int i = 0;i<vertices.length;i+=2)
//	        	vconsumer.addVertex(matrix,vertices[i],vertices[i+1],0f).setColor(r, g, b, a);
//		}));
	}
	
	
	
	@Override public void render(GuiGraphics context, DeltaTracker delta) {
		try {
			if (!Hudder.config.limitrate) compman.compile(delta);
			if (Hudder.config.shouldDrawResult()) {
	            try {
	            	if (compman.getResult()!=null)
	            		drawCompileResult(context, mc.font, compman.getResult(), Hudder.config, delta);
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
