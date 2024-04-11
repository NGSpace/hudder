package io.github.ngspace.hudder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.CompileResult;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.meta.Element;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.logging.LoggerPrintStream;

public class Hudder implements ModInitializer {
    public static String MOD_ID = "hudder";
	
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final boolean IS_DEBUG = true;
    public static List<Consumer<ATextCompiler>> precomplistners = new ArrayList<Consumer<ATextCompiler>>();
    
    WatchService watcher = null;
    WatchKey wk = null;
    
    public static CompileResult result = null;
    public static String LastFailMessage = "";
    public static ConfigInfo config = ConfigManager.getConfig();
    public static MinecraftClient ins = null;
    public static long onesecdelay = 0;

	@Override
	public void onInitialize() {
		// Makes debugging easier since it makes it red in the console.
		// It extends LoggerPrintStream to not break compatibility
		if (IS_DEBUG) System.setErr(new LoggerPrintStream("STDERR",System.err) {
			private static final Logger LOGGER = LoggerFactory.getLogger("Minecraft");
	    	@Override protected void log(@Nullable String message) {LOGGER.error("[{}]: {}", this.name, message);}
	    });
		ins = MinecraftClient.getInstance();
		
		String[] defaultfiles = {"hand","armor","hud","tutorial","basic","hud.js"};
		for (String file : defaultfiles) {
			File dest = new File(ConfigInfo.FOLDER + file);
			if (dest.exists()) continue;
			try {FileUtils.copyURLToFile(getClass().getResource("/assets/"+MOD_ID+"/" + file), dest);}
			catch (IOException e) {e.printStackTrace();}
		}
		
		log("Loading main file: " + config.mainfile + "!");
        config.readFile(config.mainfile);
        
		try {
			watcher = FileSystems.getDefault().newWatchService();
			wk = Path.of(ConfigInfo.FOLDER).register(watcher,StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {e.printStackTrace();}
    	
//    	ServerTickEvents.END_SERVER_TICK.register(server -> {/* Maybe it'll be useful in the future  */});
    	
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
        	if (!config.enabled) return;
        	//Watch for file updates
			if (wk!=null) {
				for (WatchEvent<?> event : wk.pollEvents()) {
				    //we only register "ENTRY_MODIFY" so the context is always a Path.
				    final Path changed = (Path) event.context();
				    if (changed.toString().equals("hud.json")) {
				    	config.readConfig();
						showToast(ins,Text.literal("Refreshed Config file!").formatted(Formatting.BOLD),
								Text.literal("\u00A7aLoaded File"));
				    } else {
						log(changed.getFileName() + " has changed! Refreshing hud!");
						config.readFile(changed.toString());
						showToast(ins, Text.literal("Refreshing "+changed.getFileName()+'!')
								.formatted(Formatting.BOLD), Text.literal("\u00A7aLoaded File"));
				    }
				}
				// reset the key
				if (!wk.reset()) {
					wk = null;
					error("Unable to watch for changes in File!");
					showToast(ins,Text.literal("\u00A74Failed to reload files!").formatted(Formatting.BOLD),
							Text.literal("\u00A7c"));
				}
			}
		});

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
        	try {
        		if (ins.player!=null&&config.enabled) {
        			for (Consumer<ATextCompiler> con : precomplistners) {con.accept(config.compiler);}
        			result = config.compile(getTextToCompile());
        		}
			} catch (CompileException e) {
				LastFailMessage = "\u00A7c"+e.getLocalizedMessage()
					+(e.line!=-1?" at line "+(e.line+1)+" col "+e.col:"");
				result = null;
			} catch (Exception e) {
				LastFailMessage = "\u00A7cSomethine went terribly wrong: " + e.getLocalizedMessage();
				result = null;
				e.printStackTrace();
			}
		});
			
		HudRenderCallback.EVENT.register((context,i) -> {
			if (config.shouldDrawResult(ins)) {
	            try {
	            	if (result!=null) drawCompileResult(context, ins.textRenderer, result, config);
	            	else renderText(context, LastFailMessage, 1, 1, 0xffff0000, 1, true);
				} catch (Exception e) {
					renderText(context, e.getLocalizedMessage(), 1, 1, 0xffff0000, 1, true);
				}
			}
        });
		log(MOD_ID+" has been loaded!");
	}
	public static void showToast(MinecraftClient CLIENT, Text title, Text content) {
		CLIENT.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,title,content));
	}
	
	public void drawCompileResult(DrawContext context, TextRenderer renderer, CompileResult text, ConfigInfo info) {
        int color = info.color;
        boolean shadow = info.shadow;
        
        String nlr = "\r?\n";
        /* Top Left */
        String[] lines = text.TopLeftText.split(nlr);
        int yoff = info.yoffset;
        int xoff = info.xoffset;
        for (String txt : lines) {
        	renderText(context, txt, xoff, yoff, color, text.TLScale, shadow);
        	yoff+=info.lineHeight * text.TLScale;
        }
        
        /* Bottom Left */
        String[] BL = text.BottomLeftText.split(nlr);
        yoff = (int) (context.getScaledWindowHeight() - countLines(text.BottomLeftText) *
        		info.lineHeight * text.BLScale - info.yoffset + 1);
        xoff = info.xoffset;
        for (String txt : BL) {
        	renderText(context, txt, xoff, yoff, color, text.BLScale, shadow);
        	yoff+=info.lineHeight * text.BLScale;
        }
        
        /* Top Right */
        String[] TR = text.TopRightText.split(nlr);
        yoff = info.yoffset;
        xoff = 300 - info.xoffset;
        for (String txt : TR) {
        	xoff = (int) (context.getScaledWindowWidth() - renderer.getWidth(txt) * text.TRScale - info.xoffset);
        	renderText(context, txt, xoff, yoff, color, text.TRScale, shadow);
        	yoff+=info.lineHeight * text.TRScale;
        }
        
        /* Bottom Right */
        String[] BR = text.BottomRightText.split(nlr);
        yoff = (int) (context.getScaledWindowHeight() - countLines(text.BottomRightText) *
        		info.lineHeight * text.BRScale - info.yoffset + 1);
        xoff = 300 - info.xoffset;
        for (String txt : BR) {
        	xoff = (int) (context.getScaledWindowWidth() - renderer.getWidth(txt) * text.BRScale - info.xoffset);
        	renderText(context, txt, xoff, yoff, color, text.BRScale, shadow);
        	yoff+=info.lineHeight * text.BRScale;
        }
        
        for (Element e : text.elements) e.RenderElement(context);
    }
    public int countLines(String what) {
        int count = 1;
        for (int i = 0; i<what.length();i++) if (what.charAt(i) == '\n') count++;
        return count;
    }

	public static void renderText(DrawContext context, String text, int x, int y, int color, float scale, 
			boolean shadow) {
//		if (background) {
//	        BufferBuilder bgBuilder = Tessellator.getInstance().getBuffer();
//	        bgBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
//	        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
//	        bgBuilder.vertex(matrix, x, y+10f, 0.0F).color(backgroundColor).next();
//	        bgBuilder.vertex(matrix, x+10f, y+10f, 0.0F).color(backgroundColor).next();
//	        bgBuilder.vertex(matrix, x+10f, y, 0.0F).color(backgroundColor).next();
//	        bgBuilder.vertex(matrix, x, y, 0.0F).color(backgroundColor).next();
//	        BufferRenderer.drawWithGlobalProgram(bgBuilder.end());
//		}
        if (scale != 1.0f) {
            MatrixStack matrixStack = context.getMatrices();
            matrixStack.push();
            matrixStack.translate(x, y, 0);
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(-x, -y, 0);
            context.drawText(ins.textRenderer, text, x, y, color, shadow);
            matrixStack.pop();
        } else context.drawText(ins.textRenderer, text, x, y, color, shadow);
    }
	
    private String getTextToCompile() {return config.getFile(config.mainfile);}
	public static void addPreCompilerListener(Consumer<ATextCompiler> consumer) {precomplistners.add(consumer);}
	public static void log(Object str) {LOGGER.info(String.valueOf(str));}
	public static void warn(Object str) {LOGGER.warn(String.valueOf(str));}
	public static void error(Object str) {LOGGER.error(String.valueOf(str));}
	public static void debug(Object str) {LOGGER.debug(String.valueOf(str));}
	public static void alert(Object str) {ins.player.sendMessage(Text.of(String.valueOf(str)));}
}