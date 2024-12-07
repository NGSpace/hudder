package io.github.ngspace.hudder;

import java.io.File;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ngspace.hudder.hudder.HudCompilationManager;
import io.github.ngspace.hudder.hudder.HudderRenderer;
import io.github.ngspace.hudder.hudder.HudderTickEvent;
import io.github.ngspace.hudder.hudder.config.HudderConfig;
import io.github.ngspace.hudder.utils.HudFileUtils;
import io.github.ngspace.hudder.utils.testing.HudderUnitTestingCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.server.LoggedPrintStream;
/**
 * <h1>If you expect any comments or JavaDocs explaining the bug-filled shithole I call "my code"
 * then you're gonna have a bad time.</h1>
 */
public class Hudder implements ModInitializer {
	
    private static final Logger LOGGER = LoggerFactory.getLogger("hudder");
	
    
    
    /**
     * Whether or not debug mode is enabled
     */
    public static boolean IS_DEBUG = false;
    /**
     * Hudder's config
     */
    public static HudderConfig config;

    
    
    /**
     * Errors usually happen beyond this point
     * @throws Exception Because I fuck up a lot.
     */
	@Override public void onInitialize() {
		config = new HudderConfig(new File(HudFileUtils.FOLDER + "hud.json"));

		if (IS_DEBUG) {
			log("HUDDER'S DEBUG MODE IS TURNED ON");
			log(System.err.getClass().getCanonicalName());
			// Makes debugging easier since it makes errors red in the console.
			// It extends LoggerPrintStream to not break compatibility
			System.setErr(new LoggedPrintStream("STDERR",System.err) {
				private static final Logger wk = LoggerFactory.getLogger("Minecraft");
		    	@Override protected void logLine(@Nullable String string) {wk.error("[{}]: {}", name, string);}
		    });
			// Enable unit testing.
			ClientCommandRegistrationCallback.EVENT.register(new HudderUnitTestingCommand());
		}

		
		HudFileUtils.makeDefaultHud();
		ClientTickEvents.START_CLIENT_TICK.register(new HudderTickEvent());
        
		var compman = new HudCompilationManager();
		ClientTickEvents.END_CLIENT_TICK.register(compman);
        HudRenderCallback.EVENT.register(new HudderRenderer(compman));
		
		log("Hudder has finished loading!");
	}
	
	
	
	public static void showToast(Component title, Component content) {
		Minecraft.getInstance().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PERIODIC_NOTIFICATION,title,content));
	}
	public static void showWarningToast(Component title, Component content) {
		Minecraft.getInstance().getToastManager().addToast(new SystemToast(new SystemToast.SystemToastId(10000L),title,content));
	}
	
	
	
	public static void showToast(Component title) {showToast(title, Component.keybind(""));}
	
	public static void log  (Object str) {LOGGER.info (String.valueOf(str));}
	public static void warn (Object str) {LOGGER.warn (String.valueOf(str));}
	public static void error(Object str) {LOGGER.error(String.valueOf(str));}
	public static void debug(Object str) {LOGGER.debug(String.valueOf(str));}
	@SuppressWarnings("resource")
	public static void alert(Object str) {
		Minecraft.getInstance().player.displayClientMessage(Component.keybind(String.valueOf(str)),false);
	}
}