package io.github.ngspace.hudder.compilers.v2runtime.values;

import java.util.Arrays;
import java.util.Objects;

import io.github.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.meta.MethodValue;
import io.github.ngspace.hudder.util.MathUtils;

import static io.github.ngspace.hudder.compilers.v2runtime.values.V2Values.of;

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
	
	public boolean isComparison = false;
	public V2Value comparison1;
	public String operator;
	public V2Value comparison2;
	
	/**
	 * Use {@code V2Values.of(value, compiler)} instead.
	 */
	public V2Value(String valuee, AVarTextCompiler compiler) {
		super(valuee.toLowerCase().trim(), compiler);
		
		String value = valuee.toLowerCase().trim();
		
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
		
		String[] tempValues = new String[0];
		char c;
		StringBuilder mathvalue = new StringBuilder();
		for (int i = 0;i<value.length();i++) {
			c = value.charAt(i);
			if (c=='('&&mathvalue.isEmpty()) {
				int parentheses = 0;
				i++;
				for (;i<value.length();i++) {
					c = value.charAt(i);
					if (c=='(') parentheses++;
					if (c==')') {parentheses--;if (parentheses==-1) break;}
					mathvalue.append(c);
				}
				System.out.println(mathvalue);
				continue;
			}
			if (c=='+'||c=='-'||c=='*'||c=='/'||c=='%'||c=='^') {
				tempValues = addToArray(tempValues, mathvalue.toString());
				operations = addToArray(operations, c);
				mathvalue.setLength(0);
				continue;
			}
			mathvalue.append(c);
		}
		tempValues = addToArray(tempValues, mathvalue.toString());
		if (tempValues.length>1) {
			isMath = true;
			for (String tempVal : tempValues) values = addToArray(values, of(tempVal, compiler));
		} else {
			operator = compiler.getOperator(value);
			var v = value.split(operator,2);
			comparison1 = of(v[0], compiler);
			comparison2 = of(v[1], compiler);
			isComparison = true;
		}
	}
	
	public boolean compare(V2Value other, String comparisonOperator) throws CompileException {
		Object val1 = get();
		Object val2 = other.get();
		if (val1 instanceof Number num1 && val2 instanceof Number num2) {
			double dou1 = num1.doubleValue();
			double dou2 = num2.doubleValue();
			return switch (comparisonOperator) {
				case "==" -> dou1==dou2;
				case "!=" -> dou1==dou2;
				case ">=" -> dou1>=dou2;
				case "<=" -> dou1<=dou2;
				case ">"  -> dou1> dou2;
				case "<"  -> dou1< dou2;
				default -> throw new IllegalArgumentException("Unknown comparasion operator: " + comparisonOperator);
			};
		} else {
			if (comparisonOperator.equals("==")) return  Objects.equals(val1, val2);
			if (comparisonOperator.equals("!=")) return !Objects.equals(val1, val2);
		}
		return false;
	}
	
	/**
	 * Process the value and return it as an Object.
	 * Should ideally be overwritten by anyone extending this class
	 * @return an Object of any kind.
	 * @throws CompileException
	 */
	public Object get() throws CompileException {
		if (isStatic) return compiler.getStaticVariable(value);
		if (isDynamic) return compiler.getDynamicVariable(value);
		if (isSet) {
			Object ohsaycanyousee = setValue.get();//I'm not American, I'm just sleep deprived.
			compiler.put(setKey, ohsaycanyousee);
			return ohsaycanyousee;
		}
		if (isComparison) return comparison1.compare(comparison2, operator);
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
				else if (operations[i]=='^') result = Math.pow(result, val2);
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
//		return compiler.getVariable(value);
		throw new CompileException("Unknown value: " + value);
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
}