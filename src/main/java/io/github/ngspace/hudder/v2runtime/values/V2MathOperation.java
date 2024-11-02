package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.AV2Compiler;

public class V2MathOperation extends AV2Value {
	public AV2Value[] values = new AV2Value[0];
	public char[] operations = new char[0];
	public Object constant = null;
	
	public V2MathOperation(AV2Value[] values, char[] operations, int line, int charpos, String debugvalue,
			AV2Compiler compiler) throws CompileException {
		super(line, charpos, debugvalue, compiler);
		this.values = values;
		this.operations = operations;
		if (isConstant()) constant = get();
	}
	
	@Override public Object get() throws CompileException {
		if (constant!=null) return constant;
		
		double[] secondValues = new double[values.length];
		char[] secondsOperations = new char[operations.length];
		int realSecondValuesLength = 0;
		
		//Multiply, Divide and Modulo
		if (!values[0].hasValue()) throw new CompileException(values[0].value + " has no set value!",line,charpos);
		double result = values[0].asDouble();
		for (int i = 0;i<values.length;i++) {
			if (i==operations.length) break;
			if (!values[i+1].hasValue())
				throw new CompileException(values[i+1].value + " has no set value!",line,charpos);
			var val2 = values[i+1].asDouble();
			if      (operations[i]=='*') result = result * val2;
			else if (operations[i]=='/') result = result / val2;
			else if (operations[i]=='%') result = result % val2;
			else {
				secondValues[realSecondValuesLength] = result;
				secondsOperations[realSecondValuesLength] = operations[i];
				result = values[i+1].asDouble();
				realSecondValuesLength++;
			}
		}
		secondValues[realSecondValuesLength] = result;
		realSecondValuesLength++;
		
		//Plus and Minus

		result = secondValues[0];
		
		for (int i = 0;i<realSecondValuesLength;i++) {
			if (i==realSecondValuesLength-1) break;
			var val2 = secondValues[i+1];
			if      (secondsOperations[i]=='+') result = result + val2;
			else if (secondsOperations[i]=='-') result = result - val2;
		}
		return result;
	}
	
	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of a math operation", line, charpos);
	}
	
	@Override public boolean isConstant() throws CompileException {return false;}
}
