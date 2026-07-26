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
		builder.append(api_function_help(10, "str"));
		return HudInformation.of(builder.toString());
	}
	
	public Object api_function_help(Object... objects) {
		return "fuckall";
	}

	@Override
	public boolean setupHudSettings(NGSMCConfigCategory hudsettings) {
		return false;
	}
	
}
