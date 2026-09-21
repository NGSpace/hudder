package dev.ngspace.hudder.defaultcompilers.javascript;

import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.NativeJavaMap;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.lc.type.TypeInfo;
import org.mozilla.javascript.lc.type.TypeInfoFactory;

import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.AccessUtils;
import dev.ngspace.hudder.utils.ObjectWrapper;
import dev.ngspace.hudder.utils.ValueGetter;

public class HudderJavaScriptWrapFactory extends WrapFactory {
	
	public HudderJavaScriptWrapFactory() {
		setJavaPrimitiveWrap(false);
	}
	
	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, TypeInfo staticType) {
		if (javaObject==null
				|| javaObject instanceof Class<?>
    			|| javaObject instanceof ClassLoader
    			|| !AccessUtils.isClassAccessible(javaObject.getClass()))
			return Undefined.SCRIPTABLE_UNDEFINED;
		if (javaObject instanceof ValueGetter r) {
			return new JavaObject(scope,r,staticType) {
				@Override
				public Object getField(String name, Scriptable start) {
					Object val = r.get(name);
					return val == null ? Undefined.SCRIPTABLE_UNDEFINED : val;
				}
			};
		}
		if (javaObject instanceof ObjectWrapper r) {
			try {
				return wrapAsJavaObject(cx, scope, r.get(), staticType);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		if (javaObject instanceof List<?> l)// What an L list hahahshhxhahshxsahujahsahhsfdfihuj I am dead inside :D
			return cx.newArray(scope, l.toArray());
        if (staticType.shouldReplace() && javaObject != null) {
            staticType = TypeInfoFactory.getOrElse(scope, TypeInfoFactory.GLOBAL).create(javaObject.getClass());
        }
        if (Map.class.isAssignableFrom(staticType.asClass())) {
            return new NativeJavaMap(scope, javaObject, staticType);
        } else if (staticType.isArray()) {
            return new NativeJavaArray(scope, javaObject, staticType);
        }
		return new JavaObject(scope, javaObject, staticType);
	}
}