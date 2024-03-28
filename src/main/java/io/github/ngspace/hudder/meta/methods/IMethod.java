package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;

@FunctionalInterface
public interface IMethod {
	public void execute(ConfigInfo ci, Meta meta, ATextCompiler compiler, String... args) throws CompileException;
}
