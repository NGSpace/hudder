package io.github.ngspace.hudder.v2runtime.values;

import java.util.Objects;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.methods.MethodValue;

public abstract class V2Value extends MethodValue { protected V2Value() {}
	/**
	 * Return the current value of the Object
	 * @return an Object of any kind.
	 * @throws CompileException - failed to get value of object
	 */
	public abstract Object get() throws CompileException;
	
	/**
	 * Returns true if the variable has a value and false if it does not
	 */
	public boolean hasValue() {return true;}
	
	public boolean compare(V2Value other, String comparisonOperator) throws CompileException {
		Object val1 = get();
		Object val2 = other.get();
		boolean areNums = false;//val1 instanceof Number && val2 instanceof Number;
		double dou1 = 0;
		double dou2 = 0;
		if (val1 instanceof Number num) {
			dou1 = num.doubleValue();
			boolean otherhasval = other.hasValue();
			if (!otherhasval) {
				dou2 = other.asDoubleSafe();
			}
			if (val2 instanceof Number||!otherhasval) areNums = true;
		}
		if (val2 instanceof Number num) {
			dou2 = num.doubleValue();
			boolean otherhasval = hasValue();
			if (!otherhasval) {
				dou1 = asDoubleSafe();
			}
			if (val1 instanceof Number||!otherhasval) areNums = true;
		}
		return switch (comparisonOperator) {
			case "==" -> areNums ? dou1==dou2 :  Objects.equals(val1, val2);
			case "!=" -> areNums ? dou1!=dou2 : !Objects.equals(val1, val2);
			case ">=" -> dou1>=dou2;
			case "<=" -> dou1<=dou2;
			case ">"  -> dou1> dou2;
			case "<"  -> dou1< dou2;
			default -> throw new IllegalArgumentException("Unknown comparasion operator: " + comparisonOperator);
		};
	}
	
	
	
	
	@Override public boolean asBoolean() throws CompileException {
		Object get = get();
		if (get instanceof Boolean b) return b;
		throw new CompileException("Incorrect type \"Boolean\" for value: \""+value+"\" of type "+get.getClass().getName());
	}
	@Override public double asDouble() throws CompileException {
		Object get = get();
		if (get instanceof Number b) return b.doubleValue();
		throw new CompileException("Incorrect type \"Double\" for value: \""+value+"\" of type "+get.getClass().getName());
	}
	@Override public int asInt() throws CompileException {
		Object get = get();
		if (get instanceof Number b) return b.intValue();
		throw new CompileException("Incorrect type \"Integer\" for value: \""+value+"\" of type "+get.getClass().getName());
	}
	@Override public String asString() throws CompileException {
		Object get = get();
		if (get instanceof String b) return b;
		throw new CompileException("Incorrect type \"String\" for value: \""+value+"\" of type "+get.getClass().getName());
	}
}
