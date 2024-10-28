package io.github.ngspace.hudder.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.data_management.Advanced;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.render.RenderTickCounter;

public class HudCompilationManager {
    public static List<Consumer<ATextCompiler>> precomplistners = new ArrayList<Consumer<ATextCompiler>>();
    public static List<Consumer<ATextCompiler>> postcomplistners = new ArrayList<Consumer<ATextCompiler>>();
    public HudInformation result = null;
    
    public static String LastFailMessage = "";
    
    
    
    public HudCompilationManager() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {if (Hudder.config.limitrate) compile(null);});
	}
    
    
	
	public void compile(RenderTickCounter f) {
		try {
    		Advanced.delta = f!=null?f.getLastFrameDuration():3;
    		if (Hudder.config.shouldCompile()) {
    			for (Consumer<ATextCompiler> con : precomplistners)  con.accept(Hudder.config.compiler);
//    			var l = Instant.now();
    			result = Hudder.config.compileMainHud();
//    			Hudder.log(Duration.between(l, Instant.now()).getNano());
    			for (Consumer<ATextCompiler> con : postcomplistners) con.accept(Hudder.config.compiler);
    		}
		} catch (CompileException e) {
			LastFailMessage = e.getFailureMessage();
			result = null;
		} catch (Exception e) {
			LastFailMessage = "E: " + e.getLocalizedMessage();
			result = null;
			if (Hudder.IS_DEBUG) e.printStackTrace();
		}
	}
	
	
	
	public static void addPreCompilerListener(Consumer<ATextCompiler> consumer) {precomplistners.add(consumer);}
	public static void addPostCompilerListener(Consumer<ATextCompiler> consumer) {postcomplistners.add(consumer);}
}
