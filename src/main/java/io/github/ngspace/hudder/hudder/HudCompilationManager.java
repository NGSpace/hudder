package io.github.ngspace.hudder.hudder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.data_management.Advanced;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;

public class HudCompilationManager implements EndTick {
    public static List<Consumer<ATextCompiler>> precomplistners = new ArrayList<Consumer<ATextCompiler>>();
    public static List<Consumer<ATextCompiler>> postcomplistners = new ArrayList<Consumer<ATextCompiler>>();
    private HudInformation result = null;
    
    public static String LastFailMessage = "";
    
    
	public void compile(DeltaTracker f) {
		result = null;
		try {
    		Advanced.delta = f!=null?f.getGameTimeDeltaTicks():3;
    		if (Hudder.config.shouldCompile()) {
    			for (Consumer<ATextCompiler> con : precomplistners)  con.accept(Hudder.config.getCompiler());
    			result = Hudder.config.compileMainHud();
    			for (Consumer<ATextCompiler> con : postcomplistners) con.accept(Hudder.config.getCompiler());
    		}
		} catch (CompileException e) {
			LastFailMessage = e.getFailureMessage();
		} catch (Exception e) {
			LastFailMessage = "E: " + e.getLocalizedMessage();
			if (Hudder.IS_DEBUG) e.printStackTrace();
		}
	}
	
	
	public static void addPreCompilerListener(Consumer<ATextCompiler> consumer) {precomplistners.add(consumer);}
	public static void addPostCompilerListener(Consumer<ATextCompiler> consumer) {postcomplistners.add(consumer);}

	
	public HudInformation getResult() {return result;}
	
	@Override public void onEndTick(Minecraft client) {if (Hudder.config.limitrate) compile(null);}
}
