package dev.ngspace.hudder.v2runtime.values.operations.booloperations;

import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class V2Comparison extends AV2Value {
	public AV2Value value1;
	public String operator;
	public AV2Value value2;
	public Object constant = null;

	public V2Comparison(AV2Value value1, AV2Value value2, String operator, int line, int charpos, String debugvalue,
			AV2Compiler compiler) throws ExecutionException {
		super(line, charpos, debugvalue, compiler);
		this.value1 = value1;
		this.operator = operator;
		this.value2 = value2;
		if (isConstant()) constant = get();
	}
	@Override
	public Object get() throws ExecutionException {
		if (constant!=null) return constant;
		 return value1.compare(value2, operator);
	}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws ExecutionException {
		throw new ExecutionException("Can't change the value of a comparison between values", line, charpos);
	}
	
	@Override public boolean isConstant() throws ExecutionException {return value1.isConstant()&&value2.isConstant();}
}
