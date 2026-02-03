package io.github.ngspace.hudder.compilers.utils;

import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.lc.type.TypeInfo;
import org.mozilla.javascript.lc.type.TypeInfoFactory;

import io.github.ngspace.hudder.utils.NoAccess;
import io.github.ngspace.hudder.utils.ObjectWrapper;
import io.github.ngspace.hudder.utils.ValueGetter;
import io.github.ngspace.hudder.v2runtime.V2Runtime;

public class HudderJavaScriptWrapFactory extends WrapFactory {
	
	public HudderJavaScriptWrapFactory() {
		setJavaPrimitiveWrap(false);
	}
	
	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, TypeInfo staticType) {
		if (javaObject == V2Runtime.NULL
    			|| javaObject instanceof Class<?>
    			|| javaObject instanceof ClassLoader
    			|| javaObject.getClass().isAnnotationPresent(NoAccess.class))
			return Undefined.SCRIPTABLE_UNDEFINED;
		if (javaObject instanceof ValueGetter r) {
			return new NativeJavaObject(scope,r,TypeInfoFactory.GLOBAL.create(r.getClass()),true) {
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
		if (javaObject instanceof List<?> l)// What an L list hahahshhxhahshxsahujahsahhsfdfihuj I am dead inside :D
			return cx.newArray(scope, l.toArray());
		return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
	}
}