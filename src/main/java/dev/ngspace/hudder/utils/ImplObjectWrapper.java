package dev.ngspace.hudder.utils;

import java.util.Collection;
import java.util.stream.Stream;

import dev.ngspace.hudder.exceptions.ExecutionException;

public class ImplObjectWrapper<T> implements ObjectWrapper {
	
	private T object;

	public ImplObjectWrapper(T object) {
		this.object = object;
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
		throw new ClassCastException("Can not cast value of type " + get.getClass().getSimpleName()
				+ " to " + clazz.getSimpleName());
	}

	public static <T> ObjectWrapper[] fromArray(T[] values) {
		return Stream.of(values).map(ImplObjectWrapper<T>::new).toList().toArray(new ObjectWrapper[0]);
	}
	
}
