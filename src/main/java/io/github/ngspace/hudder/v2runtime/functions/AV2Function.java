package io.github.ngspace.hudder.v2runtime.functions;

import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public abstract class AV2Function {
	public abstract Object execute(V2Runtime runtime, String functionName, AV2Value[] args);
}
