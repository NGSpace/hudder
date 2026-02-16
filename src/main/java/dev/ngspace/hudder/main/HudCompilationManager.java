package dev.ngspace.hudder.main;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.variables.advanced.Misc;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;

public class HudCompilationManager implements EndTick {
	
	protected static Minecraft mc = Minecraft.getInstance();
	
    public static List<Consumer<AHudCompiler<?>>> precomplistners = new ArrayList<Consumer<AHudCompiler<?>>>();
    public static List<Consumer<AHudCompiler<?>>> postcomplistners = new ArrayList<Consumer<AHudCompiler<?>>>();
    private HudInformation result = null;
    
    public static String LastFailMessage = "";
    
    
	public void compile(DeltaTracker f) {
		result = null;
		try {
    		Misc.delta = f!=null?f.getGameTimeDeltaTicks():3;
    		if (Hudder.config.shouldCompile()) {
    			Misc.updateCPS();
    			for (Consumer<AHudCompiler<?>> con : precomplistners)  con.accept(Hudder.config.getCompiler());
    			result = Hudder.config.compileMainHud();
    			for (Consumer<AHudCompiler<?>> con : postcomplistners) con.accept(Hudder.config.getCompiler());
    		}
		} catch (CompileException e) {
			LastFailMessage = e.getFailureMessage();
		} catch (Exception e) {
			LastFailMessage = "E: " + e.getLocalizedMessage();
			if (Hudder.IS_DEBUG) e.printStackTrace();
		}
	}
	
	
	public static void addPreCompilerListener(Consumer<AHudCompiler<?>> consumer) {precomplistners.add(consumer);}
	public static void addPostCompilerListener(Consumer<AHudCompiler<?>> consumer) {postcomplistners.add(consumer);}

	
	public HudInformation getResult() {return result;}
	
	@Override public void onEndTick(Minecraft client) {if (Hudder.config.limitrate()) compile(null);}
}
