package dev.ngspace.hudder.utils;

import java.util.Collection;

import dev.ngspace.hudder.exceptions.ExecutionException;

public class ImplObjectWrapper<T> implements ObjectWrapper {
	
	private T object;
	private int line;
	private int col;

	public ImplObjectWrapper(T object) {
		this.object = object;
		this.line = -1;
		this.col = -1;
	}

	public ImplObjectWrapper(T object, int line, int col) {
		this.object = object;
		this.line = line;
		this.col = col;
	}

	@Override
	public Object get() throws ExecutionException {
		return object;
	}
	@Override public boolean asBoolean() throws ExecutionException {return asType(Boolean.class);}
	@Override public double asDouble() throws ExecutionException {return asType(Number.class).doubleValue();}
	@Override public String asString() throws ExecutionException {return asType(String.class);}
	
	@Override
	public Object[] asArray() throws ExecutionException {
		if (object instanceof Collection<?> c) return c.toArray();
		return (Object[]) object;
	}
	
	
	public <E> E asType(Class<E> clazz) throws ExecutionException {
		Object get = get();
		if (clazz.isInstance(get)) return clazz.cast(get);
		throw new ExecutionException("Can not cast value of type " + (get==null?"null"
				: get.getClass().getSimpleName()) + " to " + clazz.getSimpleName(), line, col);
	}

	public static <T> ObjectWrapper[] fromArray(T[] values, int line, int col) {
	    ObjectWrapper[] result = new ObjectWrapper[values.length];

	    for (int i = 0; i < values.length; i++) {
	        result[i] = new ImplObjectWrapper<>(values[i], line, col);
	    }

	    return result;
	}

	public static <T> ObjectWrapper[] fromArray(T[] values) {
		return fromArray(values, -1, -1);
	}
}
