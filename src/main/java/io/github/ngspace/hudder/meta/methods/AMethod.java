package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.util.MathUtils;

public abstract class AMethod implements IMethod {
	protected AMethod() {}
	/**
	 * Try to parse as double, if not return 0.
	 * @param obj - the object to parse
	 * @return a double representation of obj or 0.
	 */
	public static double tryParse(Object text) {return MathUtils.tryParse(text);}
	/**
	 * Try to parse as int, if not return 0.
	 * @param obj - the object to parse
	 * @return a int representation of obj or 0.
	 */
	public static int tryParseInt(Object text) {return MathUtils.tryParseInt(text);}
}
