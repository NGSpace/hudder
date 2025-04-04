package io.github.ngspace.hudder.v2runtime.values;

import java.util.Collection;
import java.util.Objects;

import io.github.ngspace.hudder.compilers.abstractions.AV2Compiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.utils.ObjectWrapper;

public abstract class AV2Value implements ObjectWrapper {
	
	
	protected final int line;
	protected final int charpos;
	public final String value;
	protected final AV2Compiler compiler;
	protected CompileState state;

	protected AV2Value(int line, int charpos, String debugvalue, AV2Compiler compiler, CompileState state) {
		this.line = line;
		this.charpos = charpos;
		this.value = debugvalue;
		this.compiler = compiler;
		this.state = state;
	}
	protected AV2Value(int line, int charpos, String debugvalue, AV2Compiler compiler) {
		this(line, charpos, debugvalue, compiler, null);
	}

	
	public boolean compare(AV2Value other, String comparisonOperator) throws CompileException {
		Object val1 = get();
		Object val2 = other.get();
		if (!other.hasValue()||!hasValue()) {
			if (comparisonOperator.equals("=="))
				return other.hasValue()==hasValue();
			else if (comparisonOperator.equals("!="))
				return other.hasValue()!=hasValue();
			else throw new CompileException("Can not compare null values using the "+comparisonOperator+" operator.");
		}
		boolean areNums = false;
		double dou1 = 0;
		double dou2 = 0;
		if (val1 instanceof Number num) {
			dou1 = num.doubleValue();
			boolean otherhasval = other.hasValue();
			if (!otherhasval) dou2 = other.asDouble();
			if (val2 instanceof Number||!otherhasval) areNums = true;
		}
		if (val2 instanceof Number num) {
			dou2 = num.doubleValue();
			boolean otherhasval = hasValue();
			if (!otherhasval)  dou1 = asDouble();
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
	
	
	
	
	@Override public boolean asBoolean() throws CompileException {return asType(Boolean.class);}
	@Override public double asDouble() throws CompileException {return asType(Number.class).doubleValue();}
	@Override public String asString() throws CompileException {return asType(String.class);}
	
	
	@Override public Object[] asArray() throws CompileException {
		Object get = get();
		if (get instanceof Collection<?> c) return c.toArray();
		return (Object[]) get;
	}
	
	
	public <T> T asType(Class<T> clazz) throws CompileException {
		Object get = get();
		if (clazz.isInstance(get)) return clazz.cast(get);
		throw new CompileException(invalidTypeMessage(clazz.getSimpleName(), value, get), line, charpos);
	}
	
	
	
	
	
	
	
	public static String invalidTypeMessage(String type, String value, Object obj) {
		return "Incorrect type \""+type+"\" for value: \""+value+"\" of type "+obj.getClass().getName();
	}
	
	public abstract void setValue(AV2Compiler compiler, Object value)
			throws CompileException, UnsupportedOperationException;

	/**
	 * Returns true if the variable has a value and false if it does not
	 * @throws CompileException 
	 */
	public boolean hasValue() throws CompileException {return true;}
	public abstract boolean isConstant() throws CompileException;
	@Override public String toString() {return value;}
}
