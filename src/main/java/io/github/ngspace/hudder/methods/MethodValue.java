package io.github.ngspace.hudder.methods;

import java.util.List;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;

public abstract class MethodValue {
	
	protected MethodValue() {}
	protected String value;
	protected AVarTextCompiler compiler;
	
	protected MethodValue(String value, AVarTextCompiler compiler) {this.value=value;this.compiler=compiler;}
	
	public String getAbsoluteValue() {return value;}
	public AVarTextCompiler getCompiler() {return compiler;}
	
	public abstract Object get() throws CompileException;
	
	public abstract String asString() throws CompileException;
	public abstract int asInt() throws CompileException;
	public abstract double asDouble() throws CompileException;
	public abstract boolean asBoolean() throws CompileException;
	public abstract List<Object> asList() throws CompileException;
	
	/**
	 * @deprecated to encourage type safety, will be removed in the future.
	 */
	@Deprecated(since = "5.0.0", forRemoval = true)
	public String asStringSafe() {
		try {
			return asString();
		} catch (Exception e) {
			try {
				return cleanDouble(asDouble());
			} catch (Exception ex) {
				try {
					return String.valueOf(asBoolean());
				} catch (Exception exc) {
					return value;
				}
			}
		}
	}
	/**
	 * @deprecated to encourage type safety, will be removed in the future.
	 */
	@Deprecated(since = "5.0.0", forRemoval = true)
	public double asDoubleSafe() {try {return asDouble();} catch (Exception e) {return 0;}}
	/**
	 * @deprecated to encourage type safety, will be removed in the future.
	 */
	@Deprecated(since = "5.0.0", forRemoval = true)
	public boolean asBooleanSafe() {try {return asBoolean();} catch (Exception e) {return false;}}
	
	@Override public String toString() {return getAbsoluteValue();}
	
	public static String cleanDouble(double d) {
	    if(d == (long) d) return Long.toString((long)d);
	    else return Double.toString((long)d);
	}

}
