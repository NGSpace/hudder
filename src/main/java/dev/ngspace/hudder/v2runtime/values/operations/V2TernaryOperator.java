package dev.ngspace.hudder.v2runtime.values.operations;

import dev.ngspace.hudder.api.compilers.compilers.AV2Compiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.values.AV2Value;

public class V2TernaryOperator extends AV2Value {
	
	private AV2Value condition;
	private AV2Value truevalue;
	private AV2Value falsevalue;

	public V2TernaryOperator(AV2Value condition, AV2Value truevalue, AV2Value falsevalue, int line, int charpos,
			String debugValue, AV2Compiler comp) {
		super(line, charpos, debugValue, comp);
		this.condition = condition;
		this.truevalue = truevalue;
		this.falsevalue = falsevalue;
	}

	@Override
	public Object get() throws ExecutionException {
		return condition.asBoolean() ? truevalue.get() : falsevalue.get();
	}
	
	@Override
	public void setValue(AV2Compiler compiler, Object value) throws ExecutionException,
			UnsupportedOperationException {
		throw new ExecutionException("Can't change the value of a Ternary operator", line, charpos);
		
	}
	
	@Override
	public boolean isConstant() throws ExecutionException {
		return false;
	}
	
}
