package io.github.ngspace.hudder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.CompileResult;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.data_management.Advanced;
import io.github.ngspace.hudder.meta.elements.Element;
import io.github.ngspace.hudder.util.HudFileUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.logging.LoggerPrintStream;

/**
 * <h1>If you expect any comments or JavaDocs explaining the bug-filled shithole I call "my code"
 * then you're gonna have a bad time.</h1>
 */
public class Hudder implements ModInitializer {
    public static String MOD_ID = "hudder";
	
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean IS_DEBUG = false;
    public static final String NL_REGEX = "\r?\n";
    public static List<Consumer<ATextCompiler>> precomplistners = new ArrayList<Consumer<ATextCompiler>>();
    public static List<Consumer<ATextCompiler>> postcomplistners = new ArrayList<Consumer<ATextCompiler>>();
    
    WatchKey wk = null;
    
    public static CompileResult result = null;
    public static String LastFailMessage = "";
    public static ConfigInfo config = ConfigManager.getConfig();
    public static MinecraftClient ins = null;
    public static final String ASSETS = "/assets/"+MOD_ID+"/";
    
    /**
     * Errors usually happen beyond this point
     * @throws Exception Because I fuck up a lot.
     */
	@SuppressWarnings("deprecation")
	@Override public void onInitialize() {

		ConfigManager.setConfig(config);
		
		if (!IS_DEBUG) IS_DEBUG = config.debug;//Should automatically turn on IS_DEBUG if there is an error reading config
		
		Hudder.log("DEBUG MODE IS SET TO:" + IS_DEBUG);
		
		// Makes debugging easier since it makes errors red in the console.
		// It extends LoggerPrintStream to not break compatibility
		if (IS_DEBUG) System.setErr(new LoggerPrintStream("STDERR",System.err) {
			private static final Logger wk = LoggerFactory.getLogger("Minecraft");
	    	@Override protected void log(@Nullable String message) {wk.error("[{}]: {}", this.name, message);}
	    });
		
		ins = MinecraftClient.getInstance();

		String[] defaultfiles = {"tutorial","hand","armor","hud","basic","hud.js","hotbar.js"};
		String[] defaulttextures = {"pointer.png","selection.png"};
		for (String file : defaultfiles) {
			File dest = new File(HudFileUtils.FOLDER, file);
			if (dest.exists()) continue;
			try {FileUtils.copyURLToFile(getClass().getResource(ASSETS + file), dest);}
			catch (IOException e) {e.printStackTrace();}
		}
		if (!new File(HudFileUtils.FOLDER + "Textures").exists()) new File(HudFileUtils.FOLDER + "Textures").mkdir();
		for (String file : defaulttextures) {
			File dest = new File(HudFileUtils.FOLDER + "Textures", file);
			if (dest.exists()) continue;
			try {FileUtils.copyURLToFile(getClass().getResource(ASSETS+"Textures/" + file), dest);}
			catch (IOException e) {e.printStackTrace();}
		}
			
		log("Loading main file: " + config.mainfile + "!");
        try {HudFileUtils.getFile(config.mainfile);}
        catch (IOException e) {log("Failed to read main file!");e.printStackTrace();}
        
		try {
			wk = Path.of(HudFileUtils.FOLDER).register(FileSystems.getDefault().newWatchService(),
					StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {e.printStackTrace();}
    	
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
        	if (!config.enabled) return;
			if (wk!=null) {
				for (WatchEvent<?> event : wk.pollEvents()) {
				    final Path changed = (Path) event.context();
				    if (changed.toString().equals("hud.json")) {
				    	config.readConfig();
						showToast(ins,Text.literal("Refreshed Config file!").formatted(Formatting.BOLD),
								Text.literal("\u00A7aLoaded File"));
				    } else {
						log(changed.getFileName() + " has changed! Clearing cache!");
						HudFileUtils.clearCache();
						showToast(ins, Text.literal("Refreshing "+changed.getFileName()+'!')
								.formatted(Formatting.BOLD), Text.literal("\u00A7aLoaded File"));
				    }
				}
				if (!wk.reset()) {
					wk = null;
					error("Unable to watch for changes in File!");
					showToast(ins,Text.literal("\u00A74Failed to reload files!").formatted(Formatting.BOLD));
				}
			}
		});

        ClientTickEvents.END_CLIENT_TICK.register(client -> {if (config.limitrate) compile(3f);});
        
		HudRenderCallback.EVENT.register((context,delta) -> {
    		if (!config.limitrate) compile(delta);
			if (config.shouldDrawResult(ins)) {
            	RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
	            try {
	            	if (result!=null) drawCompileResult(context, ins.textRenderer, result, config, delta );
	            	else renderFail(context, LastFailMessage);
				} catch (Exception e) {renderFail(context, e.getLocalizedMessage());}
            	RenderSystem.disableBlend();
			}
        });
		log(MOD_ID+" has been loaded!");
	}
	public void compile(float f) {
		try {
    		Advanced.delta = f;
    		if (config.shouldCompile(ins)) {
    			for (Consumer<ATextCompiler> con : precomplistners)  con.accept(config.compiler);
    			result = config.compile(HudFileUtils.getFile(config.mainfile));
    			for (Consumer<ATextCompiler> con : postcomplistners) con.accept(config.compiler);
    		}
		} catch (CompileException e) {
			LastFailMessage = e.getLocalizedMessage()+(e.line!=-1?" at line "+(e.line+1)+" col "+e.col:"");
			result = null;
		} catch (Exception e) {
			LastFailMessage = "E: " + e.getLocalizedMessage();
			result = null;
			if (IS_DEBUG) e.printStackTrace();
		}
	}
	public static void renderFail(DrawContext context, String FailMessage) {
		String[] lines = FailMessage.split(NL_REGEX);
		int y = 1;
		for (String line : lines) {
			renderText(context, line, 1, y, 0xFF5555, 1, false, true, 0xd6d6d6);
			y+=9;
		}
	}
	public static void showToast(MinecraftClient CLIENT, Text title, Text content) {
		CLIENT.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,title,content));
	}
	public void showToast(MinecraftClient CLIENT, Text title) {showToast(CLIENT, title, Text.of(""));}
	
	public void drawCompileResult(DrawContext context, TextRenderer renderer, CompileResult text, ConfigInfo info,
			float delta) {
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
        	renderText(context, txt, xoff, yoff, color, text.TLScale, shadow, background, bgcolor);
        	yoff+=info.lineHeight * text.TLScale;
        }
        
        /* Bottom Left */
        String[] BL = text.BottomLeftText.split(NL_REGEX);
        yoff = (int) (context.getScaledWindowHeight() - countLines(text.BottomLeftText) *
        		info.lineHeight * text.BLScale - info.yoffset + 1);
        xoff = info.xoffset;
        for (String txt : BL) {
        	renderText(context, txt, xoff, yoff, color, text.BLScale, shadow, background, bgcolor);
        	yoff+=info.lineHeight * text.BLScale;
        }
        
        /* Top Right */
        String[] TR = text.TopRightText.split(NL_REGEX);
        yoff = info.yoffset;
        xoff = 300 - info.xoffset;
        for (String txt : TR) {
        	xoff = (int) (context.getScaledWindowWidth() - renderer.getWidth(txt) * text.TRScale - info.xoffset);
        	renderText(context, txt, xoff, yoff, color, text.TRScale, shadow, background, bgcolor);
        	yoff+=info.lineHeight * text.TRScale;
        }
        
        /* Bottom Right */
        String[] BR = text.BottomRightText.split(NL_REGEX);
        yoff = (int) (context.getScaledWindowHeight() - countLines(text.BottomRightText) *
        		info.lineHeight * text.BRScale - info.yoffset + 1);
        xoff = 300 - info.xoffset;
        for (String txt : BR) {
        	xoff = (int) (context.getScaledWindowWidth() - renderer.getWidth(txt) * text.BRScale - info.xoffset);
        	renderText(context, txt, xoff, yoff, color, text.BRScale, shadow, background, bgcolor);
        	yoff+=info.lineHeight * text.BRScale;
        }
        
        for (Element e : text.elements) e.RenderElement(context,delta);
    }
    public int countLines(String what) {
        int count = 1;
        for (int i = 0; i<what.length();i++) if (what.charAt(i) == '\n') count++;
        return count;
    }

	public static void renderText(DrawContext context, String text, int x, int y, int color, float scale, 
			boolean shadow, boolean background, int backgroundColor) {
		if (background&&!"".equals(text)) {
			renderBlock(context,x-1f,y-1f,ins.textRenderer.getWidth(text)+2f,9f+1f,backgroundColor);
		}
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        if (scale != 1.0f) {
            MatrixStack matrixStack = context.getMatrices();
            matrixStack.push();
            matrixStack.translate(x, y, 0);
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(-x, -y, 0);
            context.drawText(ins.textRenderer, text, x, y, color, shadow);
            matrixStack.pop();
        } else context.drawText(ins.textRenderer, text, x, y, color, shadow);
        RenderSystem.disableBlend();
    }
	public static void renderBlock(DrawContext context, float x, float y, float width, float height, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bgBuilder = Tessellator.getInstance().getBuffer();
        bgBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        bgBuilder.vertex(matrix, x, y+height, 0f).color(color).next();
        bgBuilder.vertex(matrix, x+width, y+height, 0f).color(color).next();
        bgBuilder.vertex(matrix, x+width, y, 0f).color(color).next();
        bgBuilder.vertex(matrix, x, y, 0f).color(color).next();
        BufferRenderer.drawWithGlobalProgram(bgBuilder.end());
        bgBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        matrix = context.getMatrices().peek().getPositionMatrix();
        bgBuilder.texture(0, 0);
        BufferRenderer.drawWithGlobalProgram(bgBuilder.end());
        RenderSystem.disableBlend();
	}

	public static void addPreCompilerListener(Consumer<ATextCompiler> consumer) {precomplistners.add(consumer);}
	public static void addPostCompilerListener(Consumer<ATextCompiler> consumer) {postcomplistners.add(consumer);}
	public static void log(Object str) {LOGGER.info(String.valueOf(str));}
	public static void warn(Object str) {LOGGER.warn(String.valueOf(str));}
	public static void error(Object str) {LOGGER.error(String.valueOf(str));}
	public static void debug(Object str) {LOGGER.debug(String.valueOf(str));}
	public static void alert(Object str) {ins.player.sendMessage(Text.of(String.valueOf(str)));}
}