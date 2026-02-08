package dev.ngspace.hudder.v2runtime.values.modifiable;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class V2DynamicVar extends AV2Value {
	private V2Runtime runtime;

	public V2DynamicVar(String value, V2Runtime runtime, int line, int charpos) {
		super(line, charpos, value.toLowerCase(), runtime.compiler);
		this.runtime = runtime;
	}
	
	@Override public Object get() throws CompileException {
		return runtime.getVariable(value);
	}
	
	@Override public boolean hasValue() {
		return !(runtime.getScoped(value)==null&&compiler.get(value)==null);
	}

	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		compiler.put(this.value, value);
	}
	
	@Override public boolean isConstant() throws CompileException {return false;}

	@Override public double asDouble() throws CompileException {
		if (!hasValue()) throw new CompileException('"' + value + "\" has no set value!",line,charpos);
		return super.asDouble();
	}
}
