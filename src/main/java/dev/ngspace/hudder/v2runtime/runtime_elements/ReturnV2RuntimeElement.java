package dev.ngspace.hudder.v2runtime.runtime_elements;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class ReturnV2RuntimeElement extends AV2RuntimeElement {
	
	private AV2Value value;

	public ReturnV2RuntimeElement(String value, AV2Compiler compiler, V2Runtime runtime, int line, int charpos)
			throws CompileException {
		this.value = compiler.getV2Value(runtime, value, line, charpos);
	}
	
	@Override public boolean execute(CompileState compileState, StringBuilder builder) throws CompileException {
		compileState.setReturnValue(value.get());
		return false;
	}
	
	@Override public boolean returnsAValue() {return true;}
}
