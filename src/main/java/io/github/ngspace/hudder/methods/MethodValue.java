package io.github.ngspace.hudder.methods;

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

	public String asStringSafe() {
		try {
			return asString();
		} catch (Exception e) {
			try {
				return String.valueOf(asDouble());
			} catch (Exception ex) {
				try {
					return String.valueOf(asBoolean());
				} catch (Exception exc) {
					return value;
				}
			}
		}
	}
	public int asIntSafe() {try {return asInt();} catch (Exception e) {return 0;}}
	public double asDoubleSafe() {try {return asDouble();} catch (Exception e) {return 0;}}
	public boolean asBooleanSafe() {try {return asBoolean();} catch (Exception e) {return false;}}
	
	@Override public String toString() {return getAbsoluteValue();}
}
