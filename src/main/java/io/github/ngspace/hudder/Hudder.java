package io.github.ngspace.hudder;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.utils.HudCompilationManager;
import io.github.ngspace.hudder.utils.HudFileUtils;
import io.github.ngspace.hudder.utils.HudderRenderer;
import io.github.ngspace.hudder.utils.HudderTickEvent;
import io.github.ngspace.hudder.utils.testing.HudderUnitTestingCommand;
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
@SuppressWarnings("resource")
public class Hudder implements ModInitializer {
	
    private static final Logger LOGGER = LoggerFactory.getLogger("hudder");
    
    
    public HudderRenderer renderer = null;
    public HudCompilationManager compilationManager = new HudCompilationManager();
	
	
    public static boolean IS_DEBUG = false;
    public static ConfigInfo config = ConfigManager.getConfig();

    
    
    /**
     * Errors usually happen beyond this point
     * @throws Exception Because I fuck up a lot.
     */
	@Override public void onInitialize() {
		renderer = new HudderRenderer(compilationManager);

		ConfigManager.setConfig(config);

		if (IS_DEBUG) {
			Hudder.log("HUDDER'S DEBUG MODE IS TURNED ON");		
			// Makes debugging easier since it makes errors red in the console.
			// It extends LoggerPrintStream to not break compatibility
			System.setErr(new LoggerPrintStream("STDERR",System.err) {
				private static final Logger wk = LoggerFactory.getLogger("Minecraft");
		    	@Override protected void log(@Nullable String message) {wk.error("[{}]: {}", name, message);}
		    });
			// Enable unit testing.
			ClientCommandRegistrationCallback.EVENT.register(new HudderUnitTestingCommand());
		}

		
		HudFileUtils.makeDefaultConfig();
		ClientTickEvents.START_CLIENT_TICK.register(new HudderTickEvent());
        
        
        HudRenderCallback.EVENT.register(renderer);
		
		log("Hudder has finished loading!");
	}
	
	
	
	public static void showToast(Text title, Text content) {
		MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,title,content));
	}
	public static void showWarningToast(Text title, Text content) {
		MinecraftClient.getInstance().getToastManager().add(new SystemToast(new SystemToast.Type(10000L),title,content));
	}
	
	
	
	public static void showToast(Text title) {showToast(title, Text.of(""));}
	
	public static void log  (Object str) {LOGGER.info (String.valueOf(str));}
	public static void warn (Object str) {LOGGER.warn (String.valueOf(str));}
	public static void error(Object str) {LOGGER.error(String.valueOf(str));}
	public static void debug(Object str) {LOGGER.debug(String.valueOf(str));}
	public static void alert(Object str) {
		MinecraftClient.getInstance().player.sendMessage(Text.of(String.valueOf(str)),false);
	}
}