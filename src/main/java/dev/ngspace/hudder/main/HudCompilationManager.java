package dev.ngspace.hudder.main;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.CompilerRegistry;
import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.interfaces.PreparedCompiler;
import dev.ngspace.hudder.api.compilers.utils.HudInformation;
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
	private final CompilerRegistry registry;
	public boolean isFirstRunSinceCacheClear = true;

	public String LastFailMessage = "";
	private HudInformation mainresult = null;

	private List<Consumer<AHudCompiler<?>>> compilationlistners = new ArrayList<>();

	public HudCompilationManager(HudderConfig config, CompilerRegistry registry) {
		this.config = config;
		this.registry = registry;
		HudFileUtils.addReloadResourcesListenerFirst(()->{
			isFirstRunSinceCacheClear = true;
			for (var comp : registry.compilers())
				comp.reset();
		});
	}

	public void compileAndExecute(DeltaTracker f) {
		mainresult = null;
		try {
			Misc.delta = f != null ? f.getGameTimeDeltaTicks() : 3;
			if (config.shouldCompile()) {
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
		for (var entry : registry.entries())
			if (entry.compiler() instanceof PreparedCompiler compiler)
				compiler.prepareCompiler();
		for (Consumer<AHudCompiler<?>> con : compilationlistners) con.accept(config.getCompiler());
		HudInformation result = config.getCompiler().processAndExecuteMain(config.mainfile(), config.mainfileString());
		HudFileUtils.loadMarkedResources();
		return result;
	}

	public HudInformation compileAndExecuteSecondaryHud(AHudCompiler<?> compiler, Path path, String filename)
			throws CompileException, ExecutionException, IOException {
		return compiler.processAndExecute(path, filename);
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

	public void addCompilationListener(Consumer<AHudCompiler<?>> consumer) {
		compilationlistners.add(consumer);
	}

	public void removeCompilationListener(Consumer<AHudCompiler<?>> consumer) {
		compilationlistners.remove(consumer);
	}
}
