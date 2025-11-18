package io.github.ngspace.hudder.v2runtime;

import io.github.ngspace.hudder.compilers.utils.CompileState;

public interface CompiledV2Executor {
	public void exec(StringBuilder builder, CompileState state);
}
