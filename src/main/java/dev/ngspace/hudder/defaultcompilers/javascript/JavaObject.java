package dev.ngspace.hudder.defaultcompilers.javascript;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.lc.type.TypeInfo;

import dev.ngspace.hudder.utils.AccessUtils;

public class JavaObject extends NativeJavaObject {

	private static final long serialVersionUID = -1287993552339794784L;
	public Map<String, Method> methods;
	
	public JavaObject(Scriptable scope, Object obj, TypeInfo typeinfo) {
		super(scope, obj, typeinfo, false);
		
	}
	
	@Override
	protected void initMembers() {
		methods = Arrays.stream(javaObject.getClass().getDeclaredMethods()).collect(Collectors.toMap(Method::getName, e->e));
		super.initMembers();
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		if (methods.containsKey(name))
			return getMethod(name, start);
		else
			return getField(name, start);
	}
	
	/*
	 * Both methods return the same thing, I separated them to make it easier when extending this class.
	 */
	
	public Object getMethod(String name, Scriptable start) {
		return super.get(name, start);
	}

	public Object getField(String name, Scriptable start) {
		try {
			var field = javaObject.getClass().getDeclaredField(name);
			if (!AccessUtils.isFieldAccessible(field))
				return NOT_FOUND;
			return field.get(field);
		} catch (NoSuchFieldException | IllegalAccessException _) {
			return NOT_FOUND;
		}
	}

	@Override public String getClassName() {return javaObject.getClass().getName();}
	
	public Object getObject() {
		return javaObject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(methods);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavaObject other = (JavaObject) obj;
		return Objects.equals(methods, other.methods);
	}
}
