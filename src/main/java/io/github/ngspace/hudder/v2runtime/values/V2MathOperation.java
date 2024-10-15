package io.github.ngspace.hudder.v2runtime.values;

import io.github.ngspace.hudder.compilers.utils.CompileException;

public class V2MathOperation extends AV2Value {
	public AV2Value[] values = new AV2Value[0];
	public char[] operations = new char[0];
	
	public V2MathOperation(AV2Value[] values, char[] operations) {
		this.values = values;
		this.operations = operations;
	}
	
	@SuppressWarnings("removal")
	@Override public Double get() throws CompileException {
		
		double[] secondValues = new double[values.length];
		char[] secondsOperations = new char[operations.length];
		int realSecondValuesLength = 0;
		
		//Multiply, Divide and Modulo (Sounds like either the slogan of a dictator...)
		double result = values[0].asDoubleSafe();
		for (int i = 0;i<values.length;i++) {
			if (i==operations.length) break;
			var val2 = values[i+1].asDoubleSafe();
			if      (operations[i]=='*') result = result * val2;
			else if (operations[i]=='/') result = result / val2;
			else if (operations[i]=='%') result = result % val2;
			else {
				secondValues[realSecondValuesLength] = result;
				secondsOperations[realSecondValuesLength] = operations[i];
				result = values[i+1].asDoubleSafe();
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
}
