package dev.ngspace.hudder.utils;

import java.util.Map;

import dev.ngspace.hudder.api.variableregistry.ComponentWrapper;
import dev.ngspace.hudder.exceptions.ExecutionException;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * A wrapper to an Object with functions to convert to each type
 */
public interface ObjectWrapper {
	/**
	 * Return the current value of the Object
	 * @return an Object of any kind.
	 * @throws ExecutionException - failed to get value of object
	 */
	public Object get() throws ExecutionException;
	
	
	
	/**
	 * Returns the current value of the Object in the form of a String
	 * @return The object as a String.
	 * @throws ExecutionException - if failed to get value or convert it to string.
	 */
	public String asString() throws ExecutionException;
	
	
	/**
	 * Returns the current value of the Object in the form of a Double
	 * @return The Object as a Double.
	 * @throws ExecutionException - if failed to get value or convert it to Double.
	 */
	public double asDouble() throws ExecutionException;
	
	
	/**
	 * Returns the current value of the Object in the form of an Object array
	 * @return The Object as an Object array.
	 * @throws ExecutionException - if failed to get value or convert it to an Object array.
	 */
	public Object[] asArray() throws ExecutionException;
	
	
	/**
	 * Returns the current value of the Object in the form of a Boolean
	 * @return The Object as a Boolean.
	 * @throws ExecutionException - if failed to get value or convert it to Boolean.
	 */
	public boolean asBoolean() throws ExecutionException;
	
	public <T> T asType(Class<T> clazz) throws ExecutionException;
	
	
	
	

	/**
	 * Returns the current value of the Object in the form of a Float
	 * @return The Object as a Float.
	 * @throws ExecutionException - if failed to get value or convert it to Float.
	 */
	public default float asFloat() throws ExecutionException {return (float) asDouble();}
	
	
	/**
	 * Returns the current value of the Object in the form of a Integer
	 * @return The Object as a Integer.
	 * @throws ExecutionException - if failed to get value or convert it to Integer.
	 */
	public default int asInt() throws ExecutionException {return (int) asDouble();}
	
	
	/**
	 * Returns the current value of the Object in the form of a Long
	 * @return The Object as a Long.
	 * @throws ExecutionException - if failed to get value or convert it to Long.
	 */
	public default long asLong() throws ExecutionException {return (long) asDouble();}
	
	
	/**
	 * Returns the current value of the Object in the form of a Map
	 * @return The Object as a Map.
	 * @throws ExecutionException - if failed to get value or convert it to Map.
	 */
	public default Map<?,?> asMap() throws ExecutionException {return asType(Map.class);}
	
	

	
	
	/**
	 * Returns the current value of the Object in the form of a Boolean
	 * @return The Object as a Boolean.
	 * @throws ExecutionException - if failed to get value or convert it to Boolean.
	 */
	public default float[] asFloatArray() throws ExecutionException {
		Object[] objarr = asArray();
		float[] floatarr = new float[objarr.length];
		for (int i = 0;i<objarr.length;i++)
			floatarr[i] = ((Number)objarr[i]).floatValue();//Unchecked casting but idc
		return floatarr;
	}
	
	

	
	
	/**
	 * Returns the current value of the Object in the form of a Component.
	 * 
	 * If the value is already a Component or a ComponentWrapper then return the Component itself
	 * If the value is a String then return a literal of the value's toString().
	 * 
	 * @return The Object represented as a Component.
	 * @throws ExecutionException - if failed to get value or convert it to Component.
	 */
	public default Component asComponent() throws ExecutionException {
		Object value = get();
		if (value instanceof Component comp) return comp;
		if (value instanceof ComponentWrapper wrapper) return wrapper.component;
		return Component.literal(String.valueOf(value));
	}
	
	
	
	public default Identifier asIdentifier() throws ExecutionException {
		Object value = get();
		if (value instanceof Identifier comp) return comp;
		return Identifier.parse(String.valueOf(value));
	}
	
	
	
	public abstract String toString();
}
