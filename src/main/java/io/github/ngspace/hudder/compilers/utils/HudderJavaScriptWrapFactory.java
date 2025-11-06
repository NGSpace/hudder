package io.github.ngspace.hudder.compilers.utils;

import java.math.BigInteger;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;

import io.github.ngspace.hudder.utils.NoAccess;
import io.github.ngspace.hudder.utils.ObjectWrapper;
import io.github.ngspace.hudder.utils.ValueGetter;
import io.github.ngspace.hudder.v2runtime.V2Runtime;

public class HudderJavaScriptWrapFactory extends WrapFactory {
	
	public HudderJavaScriptWrapFactory() {
//		setJavaPrimitiveWrap(false);
	}
	
	@Override
	public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
        if (obj instanceof String
                || obj instanceof Boolean
                || obj instanceof Integer
                || obj instanceof Byte
                || obj instanceof Short
                || obj instanceof Long
                || obj instanceof Float
                || obj instanceof Double
                || obj instanceof BigInteger) {
            return obj;
        } else if (obj instanceof Character) {
            return String.valueOf(((Character) obj).charValue());
        }
		return super.wrap(cx, scope, obj, staticType);
	}
	
	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		if (javaObject == V2Runtime.NULL
    			|| javaObject instanceof Class<?>
    			|| javaObject instanceof ClassLoader
    			|| javaObject.getClass().isAnnotationPresent(NoAccess.class))
			return Undefined.SCRIPTABLE_UNDEFINED;
		if (javaObject instanceof ValueGetter r) {
			return new NativeJavaObject(scope,r,r.getClass(),true) {
				private static final long serialVersionUID = -6145385781375908982L;

				@Override public String getClassName() {return r.getClass().getName();}
			    @Override public Object get(String name, Scriptable start) {
			    	var v = r.get(name);
			    	if (v==null||v==NOT_FOUND) return super.get(name, start);
			        return v;
			    }
			};
		}
		if (javaObject instanceof ObjectWrapper r) {
			try {
				return wrapAsJavaObject(cx, scope, r.get(), staticType);
			} catch (CompileException e) {
				e.printStackTrace();
			}
		}
		if (javaObject instanceof List<?> l) {
//			Object[] array = l.stream()
//		            .map(e -> Context.getCurrentContext().getWrapFactory().wrap(
//		                     Context.getCurrentContext(),
//		                     ScriptableObject.getTopLevelScope(scope),
//		                     e, e != null ? e.getClass() : null))
//		            .toArray();
//			System.out.println(array[2].getClass());
			return cx.newArray(scope, l.toArray());
//			return new NativeArray(array);
		}
		return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
	}
	
}
