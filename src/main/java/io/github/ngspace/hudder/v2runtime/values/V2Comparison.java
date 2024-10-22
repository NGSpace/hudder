package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2Comparison extends AV2Value {
	public AV2Value value1;
	public String operator;
	public AV2Value value2;

	public V2Comparison(AV2Value value1, AV2Value value2, String operator, int line, int charpos) {super(line, charpos);
		this.value1 = value1;
		this.operator = operator;
		this.value2 = value2;
	}
	@Override
	public Object get() throws CompileException {
		 return value1.compare(value2, operator);
	}
	
	@Override public void setValue(AVarTextCompiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of a comparison between values", line, charpos);
	}
	
}
