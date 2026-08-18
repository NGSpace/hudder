package dev.ngspace.hudder.hudderv3;


import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;

public interface GeneratedCompiler {
	public V3HudInformation execute(HudderConfig config, String text, String filename) throws ExecutionException;
}
