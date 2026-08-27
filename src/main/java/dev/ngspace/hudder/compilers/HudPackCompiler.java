package dev.ngspace.hudder.compilers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudpacks.CachedPack;
import dev.ngspace.hudder.hudpacks.HudPack;
import dev.ngspace.hudder.hudpacks.HudPackHudState;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;

public class HudPackCompiler extends AHudCompiler<CachedPack> {
	
	public HudPackCompiler(HudderConfig config) {
		super(config, new AtomicReference<>(), new HashMap<>());
	}

	HashMap<String, CachedPack> hudpacks = new HashMap<String, CachedPack>();
	public ArrayElementManager elms = new ArrayElementManager();

	@Override
	public CachedPack processFile(String filepath) throws CompileException {
		elms.clear();
		if (hudpacks.containsKey(filepath)) {
			CachedPack pack = hudpacks.get(filepath);
			if (pack.exception()!=null) {
				if (pack.exception() instanceof CompileException ce)
					throw ce;
				throw new CompileException(pack.exception());
			}
			return pack;
		}
		try {
			hudpacks.put(filepath, new CachedPack(new HudPack(config, HudFileUtils.FOLDER + filepath, this), null));
		} catch (IOException e) {
			e.printStackTrace();
			hudpacks.put(filepath, new CachedPack(null, e));
			throw new CompileException(e);
		}
		return hudpacks.get(filepath);
	}

	@Override
	public HudInformation execute(CachedPack pack, String filename) throws ExecutionException {
		if (pack==null||pack.pack()==null)
			return HudInformation.of("\u00A74Failed to load HudPack: " + filename);
		try {
			elms.clear();
			HudPackHudState state = new HudPackHudState();
			for (var point : pack.pack().hudpackpoints) {
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
	
	
	@Override public Object getVariable(String key) throws ExecutionException {
		return DataVariableRegistry.getAny(key);
	}

	@Override
	public boolean setupHudSettings(NGSMCConfigCategory hudsettings) {
		try {
			CachedPack mainhudpack = processFile(config.mainfile());
			
			if (mainhudpack!=null
					&&mainhudpack.pack().hasSettings()) {
				for (String setting : mainhudpack.pack().getSettingsKeys()) {
					hudsettings.addOption(mainhudpack.pack().buildSetting(setting));
				}
				return true;
			}
		} catch (CompileException e) {
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
	public void resetState() throws IOException {
		for (var pack : hudpacks.values())
			if (pack.pack()!=null)
				pack.pack().close();
		hudpacks.clear();
		super.resetState();
	}
	
}
