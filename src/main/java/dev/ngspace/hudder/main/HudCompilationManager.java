package dev.ngspace.hudder.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.variables.advanced.Misc;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;

public class HudCompilationManager implements EndTick {

	private final HudderConfig config;
	public boolean isFirstRunSinceCacheClear = true;

	public String LastFailMessage = "";
	private HudInformation mainresult = null;

	private List<Consumer<AHudCompiler<?>>> precomplistners = new ArrayList<>();

	public HudCompilationManager(HudderConfig config) {
		this.config = config;
		HudFileUtils.addReloadResourcesListenerFirst(()->isFirstRunSinceCacheClear = true);
	}


	public void addPreCompilerListener(Consumer<AHudCompiler<?>> consumer) {
		precomplistners.add(consumer);
	}

	public void compileAndExecute(DeltaTracker f) {
		mainresult = null;
		try {
			Misc.delta = f != null ? f.getGameTimeDeltaTicks() : 3;
			if (config.shouldCompile()) {
				Misc.updateCPS();
				mainresult = compileAndExecuteMainHud();
				isFirstRunSinceCacheClear = false;
			}
		} catch (CompileException e) {
			LastFailMessage = "Compiler error: " + e.getFailureMessage();
		} catch (ExecutionException e) {
			LastFailMessage = e.getFailureMessage();
		} catch (Exception e) {
			LastFailMessage = "E: " + e.getLocalizedMessage();
			if (Hudder.IS_DEBUG) {
				e.printStackTrace();
			}
		}
	}

	public HudInformation compileAndExecuteMainHud() throws CompileException, ExecutionException, IOException {
		for (Consumer<AHudCompiler<?>> con : precomplistners) {
			con.accept(config.getCompiler());
		}
		HudInformation result = config.getCompiler().processAndExecuteMain(config.mainfile(), config.mainfile());
		HudFileUtils.loadMarkedResources();
		return result;
	}

	public HudInformation compileAndExecuteSecondaryHud(AHudCompiler<?> compiler, String filepath, String filename)
			throws CompileException, ExecutionException, IOException {
		Misc.updateCPS();
		for (Consumer<AHudCompiler<?>> con : precomplistners) {
			con.accept(config.getCompiler());
		}
		HudInformation result = compiler.processAndExecute(filepath, filename);
		HudFileUtils.loadMarkedResources();
		return result;
	}

	public HudInformation getMainResult() {
		return mainresult;
	}

	@Override
	public void onEndTick(Minecraft client) {
		if (config.limitrate()) {
			compileAndExecute(null);
		}
	}

	public void removePreCompilerListener(Consumer<AHudCompiler<?>> consumer) {
		precomplistners.remove(consumer);
	}
}
