package io.github.ngspace.hudder.hudder;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.utils.HudFileUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class HudderTickEvent implements StartTick {
	
    private WatchKey wk = null;
    
    public HudderTickEvent() {
		try {
			wk = Path.of(HudFileUtils.FOLDER).register(FileSystems.getDefault().newWatchService(),
					StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	@Override public void onStartTick(Minecraft client) {
    	if (!Hudder.config.enabled) return;
    	try {
			if (wk!=null) {
				for (WatchEvent<?> event : wk.pollEvents()) {
				    Path changed = (Path) event.context();
				    if (changed.toString().equals("hud.json")) {
				    	Hudder.config.readConfig();
				    	Hudder.showToast(Component.literal("Refreshed Config file!").withStyle(ChatFormatting.BOLD),
				    			Component.literal("\u00A7aLoaded File"));
				    } else {
				    	Hudder.log(changed.getFileName() + " has changed! Clearing cache!");
						try {
							HudFileUtils.clearFileCache();
							Hudder.showToast(Component.literal("Refreshing "+changed.getFileName()+'!')
								.withStyle(ChatFormatting.BOLD), Component.literal("\u00A7aLoaded File"));
						} catch (CompileException e) {
							Hudder.showToast( Component.literal("\\u00A74Error refreshing "+changed.getFileName()+'!')
									.withStyle(ChatFormatting.BOLD),Component.literal(e.getMessage()));
							e.printStackTrace();
						}
				    }
				}
				if (!wk.reset()) {
					wk = null;
					Hudder.error("Unable to watch for changes in File!");
					Hudder.showToast(Component.literal("\u00A74Failed to reload files!").withStyle(ChatFormatting.BOLD));
				}
			}
    	} catch (RuntimeException e) {e.printStackTrace();}
	}
}