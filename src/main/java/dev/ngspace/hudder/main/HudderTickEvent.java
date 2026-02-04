package dev.ngspace.hudder.main;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.config.HudderNGSMCConfigMenu;
import dev.ngspace.hudder.utils.HudFileUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class HudderTickEvent implements StartTick {
	
    private WatchService watcherService;
    
    public HudderTickEvent() {
		try {
			watcherService = FileSystems.getDefault().newWatchService();
		    Files.walkFileTree(Path.of(HudFileUtils.FOLDER), new SimpleFileVisitor<Path>() {
		        @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
		                throws IOException {
		            dir.register(watcherService,
							StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE,
							StandardWatchEventKinds.ENTRY_DELETE);
		            return FileVisitResult.CONTINUE;
		        }

		    });
		} catch (IOException e) {e.printStackTrace();}
	}
    
	@Override public void onStartTick(Minecraft client) {
		while (Hudder.configkeybind.consumeClick()) {
			Minecraft.getInstance().setScreen(HudderNGSMCConfigMenu.createMenu(Minecraft.getInstance().screen));
		}
    	if (!Hudder.config.enabled) return;
    	try {
    		if (watcherService==null) return;
    		WatchKey wk = watcherService.poll();
			if (wk!=null) {
				for (WatchEvent<?> event : wk.pollEvents()) {
				    Path changed = (Path) event.context();
				    try {
				    	Hudder.log(changed.getFileName() + " has changed! Refreshing files!");
						HudFileUtils.reloadResources();
						Hudder.showToast(Component.literal("Refreshed files!").withStyle(ChatFormatting.BOLD), 
							Component.literal("\u00A7a"+changed.getFileName()+" changed."));
					} catch (IOException e) {
						Hudder.showToast(Component.literal("\\u00A74Error refreshing files!")
								.withStyle(ChatFormatting.BOLD),Component.literal(e.getMessage()));
						e.printStackTrace();
					}
				    if (changed.toString().equals("hud.json")) {
				    	Hudder.config.readAndUpdateConfig();
				    	Hudder.showToast(Component.literal("Refreshed Config file!").withStyle(ChatFormatting.BOLD),
				    			Component.literal("\u00A7aLoaded File"));
				    }
				}
				if (!wk.reset()) {
					watcherService = null;
					Hudder.error("Unable to watch for changes in config folder!");
					Hudder.showToast(Component.literal("\u00A74Failed to reload files!").withStyle(ChatFormatting.BOLD),
			    			Component.literal("\u00A74Failed to reload files"));
				}
			}
    	} catch (RuntimeException e) {e.printStackTrace();}
	}
}