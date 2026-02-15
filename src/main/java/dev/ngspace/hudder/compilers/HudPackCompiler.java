package dev.ngspace.hudder.compilers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.ArrayElementManager;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.hudpacks.HudPackHudState;
import dev.ngspace.hudder.hudpacks.HudPackImpl;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;

public class HudPackCompiler extends AHudCompiler<HudPackImpl> {
	
	Map<String, HudPackImpl> hudpacks = new HashMap<String, HudPackImpl>();
	public ArrayElementManager elms = new ArrayElementManager();
	
	public HudPackCompiler() {
		HudFileUtils.addReloadResourcesListener(()->hudpacks.clear());
	}

	@Override
	public HudPackImpl processFile(String filepath) {
		elms.clear();
		if (hudpacks.containsKey(filepath))
			return hudpacks.get(filepath);
		if (filepath.endsWith(".zip")) {
			hudpacks.put(filepath, new HudPackImpl(HudFileUtils.FOLDER + filepath, this));
			return processFile(filepath);
		}
		return null;
	}

	@Override
	public HudInformation compile(HudderConfig info, HudPackImpl pack, String filename) throws CompileException {
		elms.clear();
		HudPackHudState state = new HudPackHudState();
		for (var point : pack.hudpackpoints) {
			try {
				point.execute(state);
			} catch (IOException e) {
				e.printStackTrace();
				return HudInformation.of(e.getMessage());
			}
		}
		return state.toResult(elms);
	}

	@Override
	public Object getVariable(String key) throws CompileException {
		return "ur mom (ha gottem)";
	}

	@Override
	public boolean setupHudSettings(NGSMCConfigCategory hudsettings) {
		return false;
	}
	
}
