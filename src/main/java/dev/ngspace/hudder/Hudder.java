package dev.ngspace.hudder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.ngspace.hudder.api.functionsandconsumers.HudderBuiltInFunctions;
import dev.ngspace.hudder.api.functionsandconsumers.HudderBuiltInMethods;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.FunctionAndConsumerAPI;
import dev.ngspace.hudder.main.HudCompilationManager;
import dev.ngspace.hudder.main.HudderRenderer;
import dev.ngspace.hudder.main.HudderTickEvent;
import dev.ngspace.hudder.main.config.HudderConfig;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.utils.testing.HudderUnitTestingCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.server.LoggedPrintStream;
/**
 * <h1>If you expect any comments or JavaDocs explaining the bug-filled shithole I call "my code"
 * then you're gonna have a bad time.</h1>
 */
public class Hudder implements ClientModInitializer {
	
    private static final Logger LOGGER = LoggerFactory.getLogger("hudder");



	public static String HUDDER_VERSION = "${version}";
    
    
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
	@Override public void onInitializeClient() {
		Optional<ModContainer> containerOpt = FabricLoader.getInstance().getModContainer("hudder");
		
		if (containerOpt.isPresent()) {
			HUDDER_VERSION = containerOpt.get().getMetadata().getVersion().getFriendlyString();
		}
		// If there is still no version (eg. when running through an IDE)
		if (HUDDER_VERSION.equals("${version}")) {
			// Read the version from gradle.properties
			File gradleProp = new File("../gradle.properties");
			if (gradleProp.exists()) {
				try (Scanner scanner = new Scanner(gradleProp)) {
					while (scanner.hasNext()) {
						String line = scanner.nextLine();
						if (line.startsWith("mod_version=")) {
							HUDDER_VERSION = line.substring(12);
						}
					}
				} catch (FileNotFoundException e) {
					log("Can not determine Hudder version!");
					e.printStackTrace();
				}
			}
		}
		
		log("Starting Hudder " + HUDDER_VERSION);
		
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
		}
		ClientCommandRegistrationCallback.EVENT.register(new HudderUnitTestingCommand());

		
		HudFileUtils.makeDefaultHud();
		HudderBuiltInMethods.registerMethods(FunctionAndConsumerAPI.getInstance());
		HudderBuiltInFunctions.registerFunction(FunctionAndConsumerAPI.getInstance());
		ClientTickEvents.START_CLIENT_TICK.register(new HudderTickEvent());
        
		HudCompilationManager compman = new HudCompilationManager();
		ClientTickEvents.END_CLIENT_TICK.register(compman);
        
		HudderRenderer renderer = new HudderRenderer(compman);
		HudElementRegistry.addLast(renderer.hudElementRegistryID, renderer);
        
        ClientLifecycleEvents.CLIENT_STARTED.register(c->{
			try {
				HudFileUtils.reloadResources();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
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
	public static void alert(Object str) {
		Minecraft.getInstance().player.displayClientMessage(Component.keybind(String.valueOf(str)),false);
	}
}