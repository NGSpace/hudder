package io.github.ngspace.hudder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Hudder implements ModInitializer {
    public static String MOD_ID = "hudder";
	
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    public static ClientPlayerEntity player;
    WatchService watcher = null;
    WatchKey wk = null;
    
    public static CompileResult result = null;
    public static String LastFailMessage = "";
    ConfigInfo config = ConfigManager.getConfig();
    public static MinecraftClient ins = null;
    public static long onesecdelay = 0;

	@Override
	public void onInitialize() {
		ins = MinecraftClient.getInstance();
		
		String[] defaultfiles = {"hand","armor","hud","tutorial","basic"};
		for (String file : defaultfiles) {
			File dest = new File(ConfigInfo.FOLDER + file);
			if (dest.exists()) continue;
			try {
				URL inputUrl = getClass().getResource("/assets/"+MOD_ID+"/" + file);
				FileUtils.copyURLToFile(inputUrl, dest);
			} catch (IOException e) {e.printStackTrace();}
		}
		
		LOGGER.info("Loading main file: " + config.mainfile + "!");
        config.readFile(config.mainfile);
        
		try {
			watcher = FileSystems.getDefault().newWatchService();
			wk = Path.of(ConfigInfo.FOLDER).register(watcher,StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	player = ins.player;
    	
//    	ServerTickEvents.END_SERVER_TICK.register(server -> {/* Maybe it'll be useful in the future  */});
    	
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
        	if (!config.enabled) return;
        	//Watch for file updates
			if (wk!=null) {
				for (WatchEvent<?> event : wk.pollEvents()) {
				    //we only register "ENTRY_MODIFY" so the context is always a Path.
				    final Path changed = (Path) event.context();
				    if (!changed.toString().equals("hud.json")) {
						LOGGER.info(changed.getFileName() + " has changed! Refreshing hud!");
						config.readFile(changed.toString());
						showToast(ins,MOD_ID);
				    } else {
				    	config.readConfig();
						showToast(ins,"Config");
				    }
				}
				// reset the key
				boolean valid = wk.reset();
				if (!valid) {
					wk = null;
					LOGGER.error("Unable to watch for changes in File!");
					showErrorToast(ins);
				}
			}
		});

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
        	try {
        		if (ins.player!=null&&config.enabled) result = config.compile(getTextToCompile());
			} catch (CompileException e) {
				LastFailMessage = "§c" + e.getLocalizedMessage() + " at line " + (e.line+1) + " col " + e.col;
				result = null;
//				e.printStackTrace();
			} catch (Exception e) {
				LastFailMessage = "§cSomethine went terribly wrong: " + e.getLocalizedMessage();
				result = null;
				e.printStackTrace();
			}
		});
			
		HudRenderCallback.EVENT.register((e,i) -> {
			if (config.shouldDrawResult(ins)) {
	        	player = ins.player;
	            try {
					//if (ins.currentScreen!=null)
	            	if (result!=null) drawCompileResult(e, ins.textRenderer, result, config);
	            	else renderText(e, ins.textRenderer, LastFailMessage, 1, 1, 0xffff0000, 1, true);
				} catch (Exception e1) {
					renderText(e, ins.textRenderer, LastFailMessage, 1, 1, 0xffff0000, 1, true);
				}
			}
        });
		LOGGER.info(MOD_ID+" has been loaded!");
	}
	public static void showErrorToast(MinecraftClient CLIENT) {
		CLIENT.getToastManager().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE,
				Text.literal("§4Failed to reload files!").formatted(Formatting.BOLD),
				Text.literal("§c")
		));
	}
	public static void showToast(MinecraftClient CLIENT, String filetype) {
		CLIENT.getToastManager().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE,
				Text.literal("Refreshed "+filetype+" file!").formatted(Formatting.BOLD),
				Text.literal("§aLoaded File")
		));
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
        	renderText(context, renderer, txt, xoff, yoff, color, text.TLScale, shadow);
        	yoff+=info.lineHeight * text.TLScale;
        }
        
        /* Bottom Left */
        String[] BL = text.BottomLeftText.split(nlr);
        yoff = (int) (context.getScaledWindowHeight() - countLines(text.BottomLeftText) *
        		info.lineHeight * text.BLScale - info.yoffset + 1);
        xoff = info.xoffset;
        for (String txt : BL) {
        	renderText(context, renderer, txt, xoff, yoff, color, text.BLScale, shadow);
        	yoff+=info.lineHeight * text.BLScale;
        }
        
        /* Top Right */
        String[] TR = text.TopRightText.split(nlr);
        yoff = info.yoffset;
        xoff = 300 - info.xoffset;
        for (String txt : TR) {
        	xoff = (int) (context.getScaledWindowWidth() - renderer.getWidth(txt) * text.TRScale - info.xoffset);
        	renderText(context, renderer, txt, xoff, yoff, color, text.TRScale, shadow);
        	yoff+=info.lineHeight * text.TRScale;
        }
        
        /* Bottom Right */
        String[] BR = text.BottomRightText.split(nlr);
        yoff = (int) (context.getScaledWindowHeight() - countLines(text.BottomRightText) *
        		info.lineHeight * text.BRScale - info.yoffset + 1);
        xoff = 300 - info.xoffset;
        for (String txt : BR) {
        	xoff = (int) (context.getScaledWindowWidth() - renderer.getWidth(txt) * text.BRScale - info.xoffset);
        	renderText(context, renderer, txt, xoff, yoff, color, text.BRScale, shadow);
        	yoff+=info.lineHeight * text.BRScale;
        }
        
        for (Element e : text.elements) e.RenderElement(context);
    }
    public static int countLines(String what) {
        int count = 1;
        for (int i = 0; i<what.length();i++) {
        	char c = what.charAt(i);
            if (c == '\n') count++;
        }
        return count;
    }

	public static void renderText(DrawContext context, TextRenderer textRenderer, String text,
			int x, int y, int color, float scale, boolean shadowed) {
        if (scale != 1.0f) {
            MatrixStack matrixStack = context.getMatrices();
            matrixStack.push();
            matrixStack.translate(x, y, 0);
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(-x, -y, 0);
            context.drawText(textRenderer, text, x, y, color, shadowed);
            matrixStack.pop();
        } else context.drawText(textRenderer, text, x, y, color, shadowed);
    }
	
    private String getTextToCompile() {
		return ConfigManager.getConfig().getText(config.mainfile);
	}
}