package dev.ngspace.hudder.utils;

import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;

/**
 * A wrapper to an Object with functions to convert to each type
 */
public interface ObjectWrapper {
	/**
	 * Return the current value of the Object
	 * @return an Object of any kind.
	 * @throws CompileException - failed to get value of object
	 */
	public Object get() throws ExecutionException;
	
	
	
	/**
	 * Returns the current value of the Object in the form of a String
	 * @return The object as a String.
	 * @throws CompileException - if failed to get value or convert it to string.
	 */
	public String asString() throws ExecutionException;
	
	
	/**
	 * Returns the current value of the Object in the form of a Double
	 * @return The Object as a Double.
	 * @throws CompileException - if failed to get value or convert it to Double.
	 */
	public double asDouble() throws ExecutionException;
	
	
	/**
	 * Returns the current value of the Object in the form of an Object array
	 * @return The Object as an Object array.
	 * @throws CompileException - if failed to get value or convert it to an Object array.
	 */
	public Object[] asArray() throws ExecutionException;
	
	
	/**
	 * Returns the current value of the Object in the form of a Boolean
	 * @return The Object as a Boolean.
	 * @throws CompileException - if failed to get value or convert it to Boolean.
	 */
	public boolean asBoolean() throws ExecutionException;
	
	public <T> T asType(Class<T> clazz) throws ExecutionException;
	
	
	
	

	/**
	 * Returns the current value of the Object in the form of a Float
	 * @return The Object as a Float.
	 * @throws CompileException - if failed to get value or convert it to Float.
	 */
	public default float asFloat() throws ExecutionException {return (float) asDouble();}
	
	
	/**
	 * Returns the current value of the Object in the form of a Integer
	 * @return The Object as a Integer.
	 * @throws CompileException - if failed to get value or convert it to Integer.
	 */
	public default int asInt() throws ExecutionException {return (int) asDouble();}
	
	
	/**
	 * Returns the current value of the Object in the form of a Long
	 * @return The Object as a Long.
	 * @throws CompileException - if failed to get value or convert it to Long.
	 */
	public default long asLong() throws ExecutionException {return (long) asDouble();}
	
	

	
	
	/**
	 * Returns the current value of the Object in the form of a Boolean
	 * @return The Object as a Boolean.
	 * @throws CompileException - if failed to get value or convert it to Boolean.
	 */
	public default float[] asFloatArray() throws ExecutionException {
		Object[] objarr = asArray();
		float[] floatarr = new float[objarr.length];
		for (int i = 0;i<objarr.length;i++)
			floatarr[i] = ((Number)objarr[i]).floatValue();//Unchecked casting but idc
		return floatarr;
	}
	
	
	
	public abstract String toString();
}
