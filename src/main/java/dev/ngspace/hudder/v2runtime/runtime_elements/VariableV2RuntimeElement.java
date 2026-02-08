package dev.ngspace.hudder.v2runtime.runtime_elements;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class VariableV2RuntimeElement extends AV2RuntimeElement {
	
	final AV2Value value;
	final AV2Compiler compiler;
	
	public VariableV2RuntimeElement(String value, AV2Compiler compiler, V2Runtime runtime, int line, int charpos) throws CompileException {
		this.compiler = compiler;
		this.value = compiler.getV2Value(runtime, value, line, charpos);
	}

	@Override public boolean execute(CompileState meta, StringBuilder builder) throws CompileException {
		Object val = value.get();
		if (val instanceof Number num&&num.doubleValue()==num.longValue()) val = num.longValue();
		builder.append(val);
		return true;
	}
}