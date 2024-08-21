package io.github.ngspace.hudder.compilers.v2runtime.runtime_elements;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.v2runtime.values.V2Value;
import io.github.ngspace.hudder.compilers.v2runtime.values.V2Values;
import io.github.ngspace.hudder.meta.CompileState;

public class VariableV2RuntimeElement extends AV2RuntimeElement {
	final V2Value value;
	final AVarTextCompiler compiler;
	public VariableV2RuntimeElement(String value, AVarTextCompiler compiler) {
		this.compiler = compiler;
		this.value = V2Values.of(value, compiler);
	}

	@Override public void execute(CompileState meta, StringBuilder builder) throws CompileException {
		Object val = value.get();
		if (val instanceof Number num&&num.doubleValue()%1<=0.000001) val = num.longValue();
		builder.append(val);
	}
}