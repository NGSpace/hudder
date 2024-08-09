package io.github.ngspace.hudder.compilers.hudderv2;

import java.util.Arrays;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.meta.MethodValue;
import io.github.ngspace.hudder.util.MathUtils;

public class V2Value extends MethodValue {
	protected V2Value() {}
	public boolean isStatic = false;
	public boolean isDynamic = false;
	
	public boolean isSet = false;
	public String setKey;
	public V2Value setValue;
	
	public boolean isMath = false;
	public V2Value[] values = new V2Value[0];
	public char[] operations = new char[0];
	
	protected V2Value(String value, AVarTextCompiler compiler) {
		super(value.toLowerCase(), compiler);
		
		isStatic = compiler.isStaticVariable(value.toLowerCase());
		if (isStatic) return;
		
		isDynamic = compiler.isDynamicVariable(value.toLowerCase());
		if (isDynamic) return;
		
		String[] conditionValues = value.split("=",2);
		isSet = conditionValues.length==2&&!compiler.isFirstEqualsCondition(value);
		if (isSet) {
			setKey = conditionValues[0];
			setValue = of(conditionValues[1], compiler);
			return;
		}
		
		if (compiler.getOperator(value)!=null) return;//TODO add conditions
		
		String[] tempValues = new String[0];
		char c;
		StringBuilder mathvalue = new StringBuilder();
		for (int i = 0;i<value.length();i++) {
			c = value.charAt(i);
			if (c=='+'||c=='-'||c=='*'||c=='/'||c=='%') {
				tempValues = addToArray(tempValues, mathvalue.toString());
				operations = addToArray(operations, c);
				System.out.println(mathvalue.toString() + " " + values.length + " " + operations.length);
				mathvalue.setLength(0);
				continue;
			}
			mathvalue.append(c);
		}
		tempValues = addToArray(tempValues, mathvalue.toString());
		System.out.println(mathvalue.toString() + " " + tempValues.length + " " + operations.length);
//		System.out.println(values.length);
		if (values.length>1) {
			isMath = true;
			for (String tempVal : tempValues) values = addToArray(values, of(tempVal, compiler));
		}
		System.out.println(value.toLowerCase() + " " + isMath);
	}
	
	/**
	 * Process the value and return it as an Object.
	 * Should ideally be overwritten by anyone extending this class
	 * @return an Object of any kind.
	 * @throws CompileException
	 */
	public Object get() throws CompileException {
		if (isStatic) return compiler.getStaticVariable(value);
		if (isDynamic) return compiler.getStaticVariable(value);
		if (isSet) {
			Object ohsaycanyousee = setValue.get();//I'm not American, I'm just sleep deprived.
			compiler.put(setKey, ohsaycanyousee);
			return ohsaycanyousee;
		}
		if (isMath) {
			
			double[] secondValues = new double[values.length];
			char[] secondsOperations = new char[operations.length];
			int realSecondValuesLength = 0;
			
			//Multiply, Divide and Modulo (Sounds like either the slogan of a dictator...)
			double result = MathUtils.tryParse(values[0].get());
			for (int i = 0;i<values.length;i++) {
				if (i==operations.length) break;
				var val2 = MathUtils.tryParse(values[i+1].get());
				if      (operations[i]=='*') result = result * val2;
				else if (operations[i]=='/') result = result / val2;
				else if (operations[i]=='%') result = result % val2;
				else {
					secondValues[realSecondValuesLength] = result;
					secondsOperations[realSecondValuesLength] = operations[i];
					result = MathUtils.tryParse(values[i+1].get());
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
		return compiler.getVariable(value);
	}

	public static <T> T[] addToArray(T[] arr, T t) {
		T[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}
	public static char[] addToArray(char[] arr, char t) {
		char[] newarr = Arrays.copyOf(arr, arr.length+1);
		newarr[arr.length] = t;
		return newarr;
	}

	public static class V2StringValue extends V2Value {
		public V2StringValue(String value, AVarTextCompiler compiler) {this.value=value;this.compiler=compiler;}
		@Override public Object get() throws CompileException {return value;}
	}
	public static class V2DoubleValue extends V2Value {
		double doubleVal;
		public V2DoubleValue(double value, AVarTextCompiler compiler) {
			this.value=Double.toString(value);
			this.doubleVal = value;
			this.compiler=compiler;
		}
		@Override public Object get() throws CompileException {return doubleVal;}
	}
	
	public static V2Value of(String valuee, AVarTextCompiler compiler) {
		String value = valuee.trim();
		
		//Maybe Double :3
		try {return new V2DoubleValue(Double.parseDouble(value.trim()), compiler);} catch (Exception e) {}
		
		//Maybe String :)
		if (!value.startsWith("\"")||!value.endsWith("\"")) return new V2Value(valuee, compiler);
		
		//Probably String :D
		value = value.substring(1,value.length()-1);
		StringBuilder string = new StringBuilder();
		char c;
		boolean safe = false;
		for (int i = 0;i<value.length();i++) {
			c = value.charAt(i);
			if (c=='\\'&&!safe) safe = true; else {
				if (c=='"'&&!safe) return new V2Value(valuee, compiler); //Not String ;_;
				safe = false;
				string.append(c);
			}
		}
		//String! :D
		return new V2StringValue(string.toString(), compiler);
	}
}