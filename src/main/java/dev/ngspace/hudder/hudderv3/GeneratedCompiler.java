package dev.ngspace.hudder.hudderv3;

import dev.ngspace.hudder.api.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;

public abstract class GeneratedCompiler extends AVarTextCompiler {
	
	public HudderV3Helper helper;
	
	protected GeneratedCompiler(HudderConfig config) {
		super(config);
	}

	public abstract V3HudInformation execute(HudderConfig config, String filename) throws ExecutionException;
}
