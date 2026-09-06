package dev.ngspace.hudder.api.compilers.interfaces;

import dev.ngspace.hudder.api.compilers.utils.HudInformation;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;

public interface HudEvaluator<T> {
	public T evalHud(String text, String debugname) throws CompileException;
	public HudInformation evalAndExecuteHud(String text, String debugname) throws CompileException, ExecutionException;
}
