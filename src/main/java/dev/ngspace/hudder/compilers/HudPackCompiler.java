package dev.ngspace.hudder.compilers;

import java.io.IOException;
import java.util.HashMap;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudpacks.HudPack;
import dev.ngspace.hudder.hudpacks.HudPackHudState;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.v2runtime.values.AV2Value;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;

public class HudPackCompiler extends AHudCompiler<HudPack> {
	
	HashMap<String, HudPack> hudpacks = new HashMap<String, HudPack>();
	public ArrayElementManager elms = new ArrayElementManager();
	
	public HudPackCompiler() {
		HudFileUtils.addReloadResourcesListener(()->hudpacks.clear());
	}

	@Override
	public HudPack processFile(String filepath) {
		elms.clear();
		if (hudpacks.containsKey(filepath))
			return hudpacks.get(filepath);
		try {
			hudpacks.put(filepath, new HudPack(HudFileUtils.FOLDER + filepath, this));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hudpacks.get(filepath);
	}

	@Override
	public HudInformation execute(HudderConfig info, HudPack pack, String filename) throws ExecutionException {
		if (pack==null)
			return HudInformation.of("\u00A74Failed to load HudPack: " + filename);
		try {
			elms.clear();
			HudPackHudState state = new HudPackHudState();
			for (var point : pack.hudpackpoints) {
				if (point.conditions==null||checkConditions(point.conditions))
					point.execute(state);
			}
			return state.toResult(elms);
		} catch (IOException e) {
			e.printStackTrace();
			return HudInformation.of(e.getMessage());
		}
	}

	private boolean checkConditions(AV2Value[] conditions) throws ExecutionException {
		for (AV2Value cond : conditions) {
			if (!cond.asBoolean())
				return false;
		}
		return true;
	}
	
	
	@Override public Object getVariable(String key) throws ExecutionException {
		return DataVariableRegistry.getAny(key);
	}

	@Override
	public boolean setupHudSettings(NGSMCConfigCategory hudsettings) {
		HudderConfig config = Hudder.config;
		HudPack mainhudpack = processFile(config.mainfile());
		
		if (mainhudpack!=null
				&&mainhudpack.hasSettings()) {
			for (String setting : mainhudpack.getSettingsKeys()) {
				hudsettings.addOption(mainhudpack.buildSetting(setting));
			}
			return true;
		}
		
		return false;
	}
	
}
