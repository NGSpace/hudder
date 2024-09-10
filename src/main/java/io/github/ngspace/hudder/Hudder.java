package io.github.ngspace.hudder;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.arguments.StringArgumentType;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.data_management.Advanced;
import io.github.ngspace.hudder.util.HudFileUtils;
import io.github.ngspace.hudder.util.testing.HudderUnitTest;
import io.github.ngspace.hudder.util.testing.HudderUnitTestsSuggestionProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.logging.LoggerPrintStream;
/**
 * <h1>If you expect any comments or JavaDocs explaining the bug-filled shithole I call "my code"
 * then you're gonna have a bad time.</h1>
 */
public class Hudder implements ModInitializer {
    public static final String MOD_ID = "hudder";
	
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean IS_DEBUG = false;
    public static List<Consumer<ATextCompiler>> precomplistners = new ArrayList<Consumer<ATextCompiler>>();
    public static List<Consumer<ATextCompiler>> postcomplistners = new ArrayList<Consumer<ATextCompiler>>();
    
    WatchKey wk = null;
    
    public static CompileResult result = null;
    public static String LastFailMessage = "";
    public static ConfigInfo config = ConfigManager.getConfig();
    public static MinecraftClient ins = null;
    public static final String ASSETS = "/assets/"+MOD_ID+"/";
    
    public static HudderRenderer renderer = new HudderRenderer();
    
    /**
     * Errors usually happen beyond this point
     * @throws Exception Because I fuck up a lot.
     */
	@SuppressWarnings("deprecation")
	@Override public void onInitialize() {

		ConfigManager.setConfig(config);
		
		//Should automatically turn on IS_DEBUG if there is an error reading config
		if (!IS_DEBUG) IS_DEBUG = config.debug;
		
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
						try {
							HudFileUtils.clearCache();
							showToast(ins, Text.literal("Refreshing "+changed.getFileName()+'!')
									.formatted(Formatting.BOLD), Text.literal("\u00A7aLoaded File"));
						} catch (CompileException e) {
							showToast(ins, Text.literal("\\u00A74Error refreshing "+changed.getFileName()+'!')
									.formatted(Formatting.BOLD),Text.literal(e.getMessage()));
							e.printStackTrace();
						}
				    }
				}
				if (!wk.reset()) {
					wk = null;
					error("Unable to watch for changes in File!");
					showToast(ins,Text.literal("\u00A74Failed to reload files!").formatted(Formatting.BOLD));
				}
			}
		});

        ClientTickEvents.END_CLIENT_TICK.register(client -> {if (config.limitrate) compile(null);});
        
		HudRenderCallback.EVENT.register((context,delta) -> {
    		if (!config.limitrate) compile(delta);
			if (config.shouldDrawResult(ins)) {
            	RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
	            try {
	            	if (result!=null) renderer.drawCompileResult(context, ins.textRenderer, result, config, delta);
	            	else renderer.renderFail(context, LastFailMessage);
				} catch (Exception e) {renderer.renderFail(context, e.getLocalizedMessage());}
            	RenderSystem.disableBlend();
			}
        });
		log(MOD_ID+" has been loaded!");
		
		Compilers.registerCompiler("This is a lie", "io.github.ngspace.hudder.compilers.UnaccessableTestCompiler");
		
		try {
			config.hudderTester.load(getClass().getResourceAsStream(ASSETS + "UnitTests.hudder"));
		} catch (Exception e) {
			error("Could not load unit tests");
			e.printStackTrace();
		}
		
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
			dispatcher.register(literal("huddertesting")
				.then(literal("test_all").executes(context -> {
					context.getSource().sendFeedback(config.hudderTester.testAll(config));
					return 1;
				}))
				.then(literal("test").then(argument("testname",StringArgumentType.greedyString())
				.suggests(new HudderUnitTestsSuggestionProvider()).executes(context -> {
					String testname = StringArgumentType.getString(context, "testname");
					context.getSource().sendFeedback(config.hudderTester.test(config,testname).toText(testname));
					return 1;
				})))
				.then(literal("reloadTests").executes(context -> {
					try {
						config.hudderTester.UnitTests = new HashMap<String, HudderUnitTest>();
						config.hudderTester.load(getClass().getResourceAsStream(ASSETS + "UnitTests.hudder"));
						context.getSource().sendFeedback(Text.literal("Succesfully loaded tests"));
					} catch (Exception e) {
						error("Could not load unit tests");
						e.printStackTrace();
						context.getSource().sendFeedback(Text.literal("Could not load unit tests"));
					}
					return 1;
				}))
			)
		);
	}
	
	public static void showToast(MinecraftClient CLIENT, Text title, Text content) {
		CLIENT.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,title,content));
	}
	public void showToast(MinecraftClient CLIENT, Text title) {showToast(CLIENT, title, Text.of(""));}
	
	public void compile(RenderTickCounter f) {
		try {
    		Advanced.delta = f!=null?f.getLastFrameDuration():3;
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


	public static void addPreCompilerListener(Consumer<ATextCompiler> consumer) {precomplistners.add(consumer);}
	public static void addPostCompilerListener(Consumer<ATextCompiler> consumer) {postcomplistners.add(consumer);}
	public static void log  (Object str) {LOGGER.info (String.valueOf(str));}
	public static void warn (Object str) {LOGGER.warn (String.valueOf(str));}
	public static void error(Object str) {LOGGER.error(String.valueOf(str));}
	public static void debug(Object str) {LOGGER.debug(String.valueOf(str));}
	public static void alert(Object str) {ins.player.sendMessage(Text.of(String.valueOf(str)));}
}