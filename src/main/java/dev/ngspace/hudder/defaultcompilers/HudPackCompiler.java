package dev.ngspace.hudder.defaultcompilers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.interfaces.SettingsProvider;
import dev.ngspace.hudder.api.compilers.utils.HudInformation;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudpacks.HudPack;
import dev.ngspace.hudder.hudpacks.HudPackHudState;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;

public class HudPackCompiler extends AHudCompiler<HudPack> implements SettingsProvider {
	
	public HudPackCompiler(HudderConfig config) {
		super(config, new AtomicReference<>(), new HashMap<>());
	}

	public ArrayElementManager elms = new ArrayElementManager();

	@Override
	public HudPack processFile(String filepath) throws CompileException, IOException {
		elms.clear();
		return new HudPack(config, HudFileUtils.FOLDER + filepath, this);
	}

	@Override
	public HudInformation execute(HudPack pack, String filename) throws ExecutionException {
		try {
			elms.clear();
			HudPackHudState state = new HudPackHudState();
			for (var point : pack.hudpackpoints) {
				if (point.conditions==null||checkConditions(point.conditions))
					point.execute(state);
			}
			return state.toResult(elms);
		} catch (IOException e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			throw new ExecutionException(e, -1, -1);
		}
	}

	private boolean checkConditions(Boolean[] conditions) throws ExecutionException {
		for (Boolean cond : conditions)
			if (!cond.booleanValue())
				return false;
		return true;
	}

	@Override
	public boolean setupHudSettings(NGSMCConfigCategory hudsettings) {
		try {
			HudPack mainhudpack = processFile(config.mainfile());
			
			if (mainhudpack!=null
					&&mainhudpack.hasSettings()) {
				for (String setting : mainhudpack.getSettingsKeys()) {
					hudsettings.addOption(mainhudpack.buildSetting(setting));
				}
				return true;
			}
		} catch (CompileException | IOException e) {
			// Not much to do, if the pack fails to compile there are no settings.
			if (Hudder.IS_DEBUG) e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public String[] getSupportedFileFormats() {
		return new String[] {"zip"};
	}
	
	@Override
	public boolean isValidFilePath(String filepath) {
		File file = new File(HudFileUtils.FOLDER + filepath);
		return file.isDirectory() ? new File(HudFileUtils.FOLDER + filepath+"/pack.json").exists() : super.isValidFilePath(filepath);
	}
	
	@Override
	public void reset() throws IOException {
		for (var pack : instances.values())
			pack.close();
		super.reset();
	}
	
}
