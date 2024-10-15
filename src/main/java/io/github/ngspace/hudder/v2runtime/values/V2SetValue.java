package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;

public class V2SetValue extends AV2Value {
	public String key;
	public AV2Value valueToSet;

	public V2SetValue(String key, AV2Value valueToSet, AV2Compiler compiler) {
		this.key = key;
		this.valueToSet = valueToSet;
		this.compiler = compiler;
	}
	@Override
	public Object get() throws CompileException {
		Object ohsaycanyousee = valueToSet.get();//I'm not American, I'm just sleep deprived.
		compiler.put(key, ohsaycanyousee);
		return ohsaycanyousee;
	}
	
}
