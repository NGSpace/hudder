package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;

public class V2SetValue extends AV2Value {
	public AV2Value key;
	public AV2Value valueToSet;

	public V2SetValue(AV2Value key, AV2Value valueToSet, AV2Compiler compiler, int line, int charpos) {super(line, charpos);
		this.key = key;
		this.valueToSet = valueToSet;
		this.compiler = compiler;
	}
	@Override
	public Object get() throws CompileException {
		Object ohsaycanyousee = valueToSet.get();//I'm not American, I'm just sleep deprived.
		key.setValue(compiler, ohsaycanyousee);
		return ohsaycanyousee;
	}
	@Override public void setValue(AVarTextCompiler compiler, Object value) throws CompileException {
		throw new CompileException("If you receive this message, it's a bug, report it to the creator of Hudder: 01", line, charpos);
	}
	
}
