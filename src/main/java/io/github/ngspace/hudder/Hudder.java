package io.github.ngspace.hudder;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.util.HudCompilationManager;
import io.github.ngspace.hudder.util.HudFileUtils;
import io.github.ngspace.hudder.util.HudderRenderer;
import io.github.ngspace.hudder.util.HudderTickEvent;
import io.github.ngspace.hudder.util.testing.HudderUnitTestingCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.logging.LoggerPrintStream;
/**
 * <h1>If you expect any comments or JavaDocs explaining the bug-filled shithole I call "my code"
 * then you're gonna have a bad time.</h1>
 */
public class Hudder implements ModInitializer {
	/* 01 - setting a set to a value
	 */
	
    private static final Logger LOGGER = LoggerFactory.getLogger("hudder");
    
    
    public static boolean IS_DEBUG = false;
    public static ConfigInfo config = ConfigManager.getConfig();
    public HudderRenderer renderer = null;
    
    
	public static Hudder instance;
	
	
    public HudCompilationManager compilationManager = new HudCompilationManager();
	
	private static MinecraftClient mc = MinecraftClient.getInstance();

    
    
    /**
     * Errors usually happen beyond this point
     * @throws Exception Because I fuck up a lot.
     */
	@Override public void onInitialize() {
		instance = this;
		renderer = new HudderRenderer(compilationManager);

		ConfigManager.setConfig(config);
		
		// Makes debugging easier since it makes errors red in the console.
		// It extends LoggerPrintStream to not break compatibility
		if (IS_DEBUG) {
			Hudder.log("HUDDER'S DEBUG MODE IS TURNED ON");
			System.setErr(new LoggerPrintStream("STDERR",System.err) {
				private static final Logger wk = LoggerFactory.getLogger("Minecraft");
		    	@Override protected void log(@Nullable String message) {wk.error("[{}]: {}", this.name, message);}
		    });
		}

		
		HudFileUtils.makeDefaultConfig();
		ClientTickEvents.START_CLIENT_TICK.register(new HudderTickEvent());
        
        
        HudRenderCallback.EVENT.register(renderer);
        
		ClientCommandRegistrationCallback.EVENT.register(new HudderUnitTestingCommand());
		
		log("Hudder has finished loading!");
	}
	
	
	
	public static void showToast(MinecraftClient CLIENT, Text title, Text content) {
		CLIENT.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,title,content));
	}
	public static void showWarningToast(MinecraftClient CLIENT, Text title, Text content) {
		CLIENT.getToastManager().add(new SystemToast(new SystemToast.Type(10000L),title,content));
	}
	
	
	
	public static void showToast(MinecraftClient CLIENT, Text title) {showToast(CLIENT, title, Text.of(""));}
	
	public static void log  (Object str) {LOGGER.info (String.valueOf(str));}
	public static void warn (Object str) {LOGGER.warn (String.valueOf(str));}
	public static void error(Object str) {LOGGER.error(String.valueOf(str));}
	public static void debug(Object str) {LOGGER.debug(String.valueOf(str));}
	public static void alert(Object str) {mc.player.sendMessage(Text.of(String.valueOf(str)),false);}
}