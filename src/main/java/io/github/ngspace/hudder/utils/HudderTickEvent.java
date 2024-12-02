package io.github.ngspace.hudder.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
    
	@Override public void onStartTick(MinecraftClient client) {
    	if (!Hudder.config.enabled) return;
    	try {
			if (wk!=null) {
				for (WatchEvent<?> event : wk.pollEvents()) {
				    final Path changed = (Path) event.context();
				    if (changed.toString().equals("hud.json")) {
				    	Hudder.config.readConfig();
				    	Hudder.showToast(Text.literal("Refreshed Config file!").formatted(Formatting.BOLD),
								Text.literal("\u00A7aLoaded File"));
				    } else {
				    	Hudder.log(changed.getFileName() + " has changed! Clearing cache!");
						try {
							HudFileUtils.clearFileCache();
							Hudder.showToast(Text.literal("Refreshing "+changed.getFileName()+'!')
								.formatted(Formatting.BOLD), Text.literal("\u00A7aLoaded File"));
						} catch (CompileException e) {
							Hudder.showToast( Text.literal("\\u00A74Error refreshing "+changed.getFileName()+'!')
									.formatted(Formatting.BOLD),Text.literal(e.getMessage()));
							e.printStackTrace();
						}
				    }
				}
				if (!wk.reset()) {
					wk = null;
					Hudder.error("Unable to watch for changes in File!");
					Hudder.showToast(Text.literal("\u00A74Failed to reload files!").formatted(Formatting.BOLD));
				}
			}
    	} catch (RuntimeException e) {e.printStackTrace();}
	}
	
}
