package io.github.ngspace.hudder.compilers.hudderv2.runtime_elements;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.hudderv2.V2Value;
import io.github.ngspace.hudder.meta.CompileState;

public class VariableV2RuntimeElement extends AV2RuntimeElement {
	final V2Value value;
	final AVarTextCompiler compiler;
	public VariableV2RuntimeElement(String value, AVarTextCompiler compiler) {
		this.compiler = compiler;
		this.value = getValue(value);
	}

	@Override public void execute(CompileState meta, StringBuilder builder) throws CompileException {
		Object val = value.get();
		if (val instanceof Number num&&num.doubleValue()%1==0) val = num.longValue();
		builder.append(val);
	}
	
	public V2Value getValue(String s) {return V2Value.of(s, compiler);}
}
