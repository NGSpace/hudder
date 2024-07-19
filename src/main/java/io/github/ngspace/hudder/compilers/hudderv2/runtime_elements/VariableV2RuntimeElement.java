package io.github.ngspace.hudder.compilers.hudderv2.runtime_elements;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.hudderv2.HudderV2Runtime;
import io.github.ngspace.hudder.compilers.hudderv2.V2Value;
import io.github.ngspace.hudder.meta.Meta;

public class VariableV2RuntimeElement extends AV2RuntimeElement {
	final V2Value value;
	final HudderV2Runtime runtime;
	public VariableV2RuntimeElement(String value, HudderV2Runtime runtime) {
		this.runtime = runtime;
		this.value = getValue(value);
	}

	@Override
	public void execute(Meta meta, StringBuilder builder) throws CompileException {
		builder.append(value.toObject());
	}
	
	public V2Value getValue(String s) {return new V2Value(s, runtime);}
}
