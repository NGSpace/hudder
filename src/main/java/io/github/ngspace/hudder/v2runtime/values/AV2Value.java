package io.github.ngspace.hudder.v2runtime.values;

import java.util.List;
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
	
	
	
	
	@Override public boolean asBoolean() throws CompileException {
		Object get = get();
		if (get instanceof Boolean b) return b;
		throw new CompileException(invalidTypeMessage("Boolean", value, get), line, charpos);
	}
	@Override public double asDouble() throws CompileException {
		Object get = get();
		if (get instanceof Number b) return b.doubleValue();
		throw new CompileException(invalidTypeMessage("Double", value, get), line, charpos);
	}
	
	
	@Override @SuppressWarnings("unchecked") public List<Object> asList() throws CompileException {
		return asType(List.class);
	}
	@Override public Object[] asArray() throws CompileException {return asList().toArray();}
	@Override public String asString() throws CompileException {return asType(String.class);}
	
	
	public <T> T asType(Class<T> clazz) throws CompileException {
		Object get = get();
		if (clazz.isInstance(get)) return clazz.cast(get);
		throw new CompileException(invalidTypeMessage("String", value, get), line, charpos);
	}
	
	
	
	
	
	
	
	public static String invalidTypeMessage(String type, String value, Object obj) {
		return "Incorrect type \""+type+"\" for value: \""+value+"\" of type "+obj.getClass().getName();
	}
	
	public abstract void setValue(AV2Compiler compiler, Object value) throws CompileException;

	/**
	 * Returns true if the variable has a value and false if it does not
	 */
	public boolean hasValue() {return true;}
	public abstract boolean isConstant() throws CompileException;
	@Override public String toString() {return value;}
}
