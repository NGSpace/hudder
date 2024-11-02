package io.github.ngspace.hudder.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.ngspace.hudder.compilers.utils.CompileException;

public abstract class ObjectWrapper {
	/**
	 * Return the current value of the Object
	 * @return an Object of any kind.
	 * @throws CompileException - failed to get value of object
	 */
	public abstract Object get() throws CompileException;
	
	
	public abstract String asString() throws CompileException;
	public abstract double asDouble() throws CompileException;
	public abstract Object[] asArray() throws CompileException;
	public abstract boolean asBoolean() throws CompileException;
	
	public float asFloat() throws CompileException {return (float) asDouble();}
	public int asInt() throws CompileException {return (int) asDouble();}
	public long asLong() throws CompileException {return (long) asDouble();}
	
	
	
	public float[] asFloatArray() throws CompileException {
		Object[] objarr = asArray();
		float[] floatarr = new float[objarr.length];
		for (int i = 0;i<objarr.length;i++)
			floatarr[i] = ((Number)objarr[i]).floatValue();//Unchecked casting but idc
		return floatarr;
	}
	
	public List<Object> asList() throws CompileException {return new ArrayList<Object>(Arrays.asList(asArray()));}
	
	
	
	public abstract String toString();
}
