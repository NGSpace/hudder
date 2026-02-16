package dev.ngspace.hudder.compilers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
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
		hudpacks.put(filepath, new HudPackImpl(HudFileUtils.FOLDER + filepath, this));
		return hudpacks.get(filepath);
	}

	@Override
	public HudInformation compile(HudderConfig info, HudPackImpl pack, String filename) throws CompileException {
		try {
			elms.clear();
			HudPackHudState state = new HudPackHudState();
			for (var point : pack.hudpackpoints) {
				if (point.config.condition()==null||
						Boolean.TRUE.equals(DataVariableRegistry.getBoolean(point.config.condition())))
					point.execute(state);
			}
			return state.toResult(elms);
		} catch (IOException e) {
			e.printStackTrace();
			return HudInformation.of(e.getMessage());
		}
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
