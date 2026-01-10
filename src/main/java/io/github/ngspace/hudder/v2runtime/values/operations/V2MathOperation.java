package io.github.ngspace.hudder.v2runtime.values.operations;

import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;

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

		Object[] processedValues = new Object[values.length];
		boolean should_concat = false;
		for (int i = 0;i<values.length;i++) {
			processedValues[i] = values[i].get();
			if (!(processedValues[i] instanceof Number)) should_concat = true;
		}
		if (should_concat) {
			String result = toString(processedValues[0]);
			for (int i = 0;i<operations.length;i++) {
				if (operations[i]=='+') {
					result += toString(processedValues[i+1]);
				} else throw new CompileException("Unknown operator for str concatination: "+operations[i],line,charpos);
			}
			return result;
		}
		double[] secondValues = new double[values.length];
		char[] secondsOperations = new char[operations.length];
		int realSecondValuesLength = 0;
		
		//Multiply, Divide and Modulo
		double result = getDouble(processedValues[0]);
		for (int i = 0;i<values.length;i++) {
			if (i==operations.length) break;
			var val2 = getDouble(processedValues[i+1]);
			if      (operations[i]=='*') result = result * val2;
			else if (operations[i]=='/') result = result / val2;
			else if (operations[i]=='%') result = result % val2;
			else {
				secondValues[realSecondValuesLength] = result;
				secondsOperations[realSecondValuesLength] = operations[i];
				result = getDouble(processedValues[i+1]);
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
	
	private String toString(Object object) {
		if (object instanceof Number num&&num.doubleValue()%1==0d) return String.valueOf(num.intValue());
		return String.valueOf(object);
	}

	private double getDouble(Object object) {
		return ((Number) object).doubleValue();
	}

	@Override public void setValue(AV2Compiler compiler, Object value) throws CompileException {
		throw new CompileException("Can't change the value of a math operation", line, charpos);
	}
	
	@Override public boolean isConstant() throws CompileException {
        for (AV2Value v : values) {
            if (!v.isConstant()) return false;
        }
        return true;
	}
}
