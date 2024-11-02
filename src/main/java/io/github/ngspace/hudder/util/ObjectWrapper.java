package io.github.ngspace.hudder.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.ngspace.hudder.compilers.utils.CompileException;

public interface ObjectWrapper {
	/**
	 * Return the current value of the Object
	 * @return an Object of any kind.
	 * @throws CompileException - failed to get value of object
	 */
	public Object get() throws CompileException;
	
	
	public String asString() throws CompileException;
	public double asDouble() throws CompileException;
	public Object[] asArray() throws CompileException;
	public boolean asBoolean() throws CompileException;
	
	public default float asFloat() throws CompileException {return (float) asDouble();}
	public default int asInt() throws CompileException {return (int) asDouble();}
	public default long asLong() throws CompileException {return (long) asDouble();}
	
	
	
	public default float[] asFloatArray() throws CompileException {
		Object[] objarr = asArray();
		float[] floatarr = new float[objarr.length];
		for (int i = 0;i<objarr.length;i++)
			floatarr[i] = ((Number)objarr[i]).floatValue();//Unchecked casting but idc
		return floatarr;
	}
	
	
	
	public default List<Object> asList() throws CompileException {
		return new ArrayList<Object>(Arrays.asList(asArray()));
	}
	
	
	
	@Override public String toString();
}
