package io.github.ngspace.hudder.meta;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.util.MathUtils;

public class MethodValue {
	protected MethodValue() {}
	protected String value;
	protected AVarTextCompiler compiler;
	public MethodValue(String value, AVarTextCompiler compiler) {
		this.value=value;
		this.compiler=compiler;
	}
	public String getAbsoluteValue() {return value;}
	@Override public String toString() {return getAbsoluteValue();}
	public AVarTextCompiler getCompiler() {return compiler;}
	
	public String asString() throws CompileException {return String.valueOf(compiler.getVariable(value.trim()));}
	public int asInt() throws CompileException {return tryParseInt(compiler.getVariable(value.trim()));}
	public double asDouble() throws CompileException {return tryParse(compiler.getVariable(value.trim()));}
	public boolean asBoolean() {return tryParseBool(compiler.get(value.trim()));}
	
	/**
	 * Try to parse as double, if not return 0.
	 * @param obj - the object to parse
	 * @return a double representation of obj or 0.
	 */
	public static double tryParse(Object obj) {return MathUtils.tryParse(obj);}
	/**
	 * Try to parse as int, if not return 0.
	 * @param obj - the object to parse
	 * @return a int representation of obj or 0.
	 */
	public static int tryParseInt(Object obj) {return MathUtils.tryParseInt(obj);}
	/**
	 * Parse as boolean.
	 * @param obj - the object to parse
	 * @return a boolean representation of object.
	 */
	public static boolean tryParseBool(Object object) {
		if (object instanceof Boolean) return (boolean) object;
		return Boolean.valueOf(String.valueOf(object));
	}
}
