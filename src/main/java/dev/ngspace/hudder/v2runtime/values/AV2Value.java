package dev.ngspace.hudder.v2runtime.values;

import java.util.Collection;
import java.util.Objects;

import dev.ngspace.hudder.api.compilers.compilers.AV2Compiler;
import dev.ngspace.hudder.api.compilers.utils.CompileState;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ObjectWrapper;

/**
 * @deprecated Use V3
 */
@Deprecated(since = "11.1.0", forRemoval = true)
@SuppressWarnings("removal")
public abstract class AV2Value implements ObjectWrapper {
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	protected final int line;
	@Deprecated(since = "11.1.0", forRemoval = true)
	protected final int charpos;
	@Deprecated(since = "11.1.0", forRemoval = true)
	public final String value;
	@Deprecated(since = "11.1.0", forRemoval = true)
	protected final AV2Compiler compiler;
	@Deprecated(since = "11.1.0", forRemoval = true)
	protected CompileState state;

	@Deprecated(since = "11.1.0", forRemoval = true)
	protected AV2Value(int line, int charpos, String debugvalue, AV2Compiler compiler, CompileState state) {
		this.line = line;
		this.charpos = charpos;
		this.value = debugvalue;
		this.compiler = compiler;
		this.state = state;
	}
	@Deprecated(since = "11.1.0", forRemoval = true)
	protected AV2Value(int line, int charpos, String debugvalue, AV2Compiler compiler) {
		this(line, charpos, debugvalue, compiler, null);
	}

	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public boolean compare(AV2Value other, String comparisonOperator) throws ExecutionException {
		Object val1 = get();
		Object val2 = other.get();
		if (!other.hasValue()||!hasValue()) {
			if (comparisonOperator.equals("=="))
				return other.hasValue()==hasValue();
			else if (comparisonOperator.equals("!="))
				return other.hasValue()!=hasValue();
			else throw new ExecutionException("Can not compare null values using the "+comparisonOperator+" operator.",
					line, charpos);
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
	
	
	
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	@Override public boolean asBoolean() throws ExecutionException {return asType(Boolean.class);}
	@Deprecated(since = "11.1.0", forRemoval = true)
	@Override public double asDouble() throws ExecutionException {return asType(Number.class).doubleValue();}
	@Deprecated(since = "11.1.0", forRemoval = true)
	@Override public String asString() throws ExecutionException {return asType(String.class);}
	
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	@Override public Object[] asArray() throws ExecutionException {
		Object get = get();
		if (get instanceof Collection<?> c) return c.toArray();
		return (Object[]) get;
	}
	
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public <T> T asType(Class<T> clazz) throws ExecutionException {
		Object get = get();
		if (clazz.isInstance(get)) return clazz.cast(get);
		throw new ExecutionException(invalidTypeMessage(clazz.getSimpleName(), value, get), line, charpos);
	}
	
	
	
	
	
	
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public static String invalidTypeMessage(String type, String value, Object obj) {
		return "Incorrect type \""+type+"\" for value: \""+value+"\" of type "+(obj!=null?obj.getClass().getName():null);
	}
	
	@Deprecated(since = "11.1.0", forRemoval = true)
	public abstract void setValue(AV2Compiler compiler, Object value) throws ExecutionException, UnsupportedOperationException;

	/**
	 * Returns true if the variable has a value and false if it does not
	 * @throws CompileException 
	 */
	@Deprecated(since = "11.1.0", forRemoval = true)
	public boolean hasValue() throws ExecutionException {return true;}
	@Deprecated(since = "11.1.0", forRemoval = true)
	public abstract boolean isConstant() throws ExecutionException;
	@Deprecated(since = "11.1.0", forRemoval = true)
	@Override public String toString() {return value;}
}
