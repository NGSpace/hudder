package dev.ngspace.hudder.compilers;

import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;

public class DynamicClass extends AVarTextCompiler {

	@Override
	public HudInformation execute(HudderConfig info, String processedfile, String filename) throws ExecutionException {
		StringBuilder builder = new StringBuilder();
		builder.append(DataVariableRegistry.getNumber("fps")+DataVariableRegistry.getNumber("fps")/3-1);
		return HudInformation.of(builder.toString());
	}

	@Override
	public boolean setupHudSettings(NGSMCConfigCategory hudsettings) {
		return false;
	}
	
}
