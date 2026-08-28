package dev.ngspace.hudder.v2runtime.values.modifiable;

import dev.ngspace.hudder.api.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class V2TempDynamicVar extends AV2Value {

	public V2TempDynamicVar(String value, AV2Compiler compiler, int line, int charpos) {
		super(line, charpos, value.toLowerCase(), compiler);
	}
	
	@Override public Object get() throws ExecutionException {
		Object o = compiler.tempVariables.get(value);
		if (o==null) return 0;
		return o;
	}

	@Override public void setValue(AV2Compiler compiler, Object value) throws ExecutionException {
		compiler.tempVariables.put(this.value, value);
	}
	
	@Override public boolean isConstant() throws ExecutionException {return false;}
	

}
