package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;

public class V2Comparison extends AV2Value {
	public AV2Value value1;
	public String operator;
	public AV2Value value2;
	public Object constant = null;

	public V2Comparison(AV2Value value1, AV2Value value2, String operator, int line, int charpos)
			throws CompileException {super(line, charpos);
		this.value1 = value1;
		this.operator = operator;
		this.value2 = value2;
		if (isConstant()) constant = get();
	}
	@Override
	public Object get() throws CompileException {
		if (constant!=null) return constant;
		 return value1.compare(value2, operator);
	}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of a comparison between values", line, charpos);
	}
	
	@Override public boolean isConstant() throws CompileException {return value1.isConstant()&&value2.isConstant();}
}
