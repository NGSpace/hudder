package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2Comparison extends AV2Value {
	public AV2Value value1;
	public String operator;
	public AV2Value value2;

	public V2Comparison(AV2Value value1, AV2Value value2, String operator) {
		this.value1 = value1;
		this.operator = operator;
		this.value2 = value2;
	}
	@Override
	public Object get() throws CompileException {
		 return value1.compare(value2, operator);
	}
	
}
