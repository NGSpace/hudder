package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.util.MathUtils;

public abstract class AMethod implements IMethod {
	protected AMethod() {}
	
	public static double tryParse(Object text) {
		return MathUtils.tryParse(text);
	}
	
	public static int tryParseInt(Object text) {
		return MathUtils.tryParseInt(text);
	}
}
