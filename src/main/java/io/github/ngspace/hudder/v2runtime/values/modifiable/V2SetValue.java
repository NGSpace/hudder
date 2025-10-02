package io.github.ngspace.hudder.v2runtime.values.modifiable;

import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

public class V2SetValue extends AV2Value {
	public AV2Value key;
	public AV2Value v2value;

	public V2SetValue(AV2Value key, AV2Value v2value, AV2Compiler compiler, int line, int charpos, String debugvalue) {
		super(line, charpos, debugvalue,compiler);
		this.key = key;
		this.v2value = v2value;
	}
	@Override public Object get() throws CompileException {
		Object value = v2value.get();
		key.setValue(compiler, value);
		return value;
	}
	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException(
				"This error is a bug, please report it, \ndbg inf: \"V2SetValue.setValue(V2Comp,o)\", V2Comp:\""
				+ compiler.getClass().getSimpleName() + "\", o:" + value, line, charpos);
	}
	@Override public boolean isConstant() throws CompileException {return v2value.isConstant();}
	
}
