package io.github.ngspace.hudder.compilers.hudderv2;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.meta.MethodValue;

public class V2Value extends MethodValue {
	protected V2Value() {}
	boolean isStatic = false;
	boolean isDynamic = false;
	public V2Value(String value, AVarTextCompiler compiler) {
		super(value.toLowerCase(), compiler);
		isStatic = compiler.isStaticVariable(value.toLowerCase());
		isDynamic = compiler.isDynamicVariable(value.toLowerCase());
		System.out.println(value.toLowerCase() + " " + isStatic + " " + isDynamic);
	}
	public Object toObject() throws CompileException {
		if (isStatic) return compiler.getStaticVariable(value);
		if (isDynamic) return compiler.getStaticVariable(value);
		return compiler.getVariable(value);
	}
}