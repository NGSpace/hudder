package io.github.ngspace.hudder.compilers.utils;

import java.io.IOException;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.WrappedException;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.utils.ObjectWrapper;
import io.github.ngspace.hudder.utils.ValueGetter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class JavaScriptEngine implements IScriptingLanguageEngine {
	
	protected static MinecraftClient mc = MinecraftClient.getInstance();
	
	
	Context cx;
	ScriptableObject scope;
	public JavaScriptEngine() {
        cx = Context.enter();
        cx.setWrapFactory(new WrapFactory() {
        	@Override
        	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
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
        		return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
        	}
        });
        cx.setLanguageVersion(Context.VERSION_ES6);//Beta features
        cx.setOptimizationLevel(9);
        
        scope = cx.initSafeStandardObjects();
		
		var JavaScriptIO = new JavaScriptIO();
		
		insertObject(JavaScriptIO, "console");
		insertObject(JavaScriptIO, "hudder" );
		
		bindConsumer(s->JavaScriptIO.log  (s[0]), "log"  );
		bindConsumer(s->JavaScriptIO.warn (s[0]), "warn" );
		bindConsumer(s->JavaScriptIO.error(s[0]), "error");
		bindConsumer(s->JavaScriptIO.alert(s[0]), "alert");
	}

	@Override public void bindFunction(ScriptFunction function, String... names) {
        Function func = new BaseFunction() {
            private static final long serialVersionUID = 1L;
			@Override public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				try {
					ObjectWrapper[] vals = new ObjectWrapper[args.length];
					for (int i = 0;i<args.length;i++) {
						vals[i] = new JavaScriptValue(args[i]);
					}
					return function.exec(vals);
				} catch (Exception e) {
					throw new WrappedException(e);
				}
            }
        };
        for (String name : names) scope.put(name, scope, func);
	}
	@Override public void bindConsumer(ScriptConsumer consumer, String... names) {
		bindFunction(e->{consumer.exec(e);return Undefined.instance;},names);
	}
	
	
	
	@Override public ObjectWrapper readVariable(String name) {
		Object val = scope.get(name, scope);
		if (val==Scriptable.NOT_FOUND) return null;
		return new JavaScriptValue(val);
	}
	@Override public ObjectWrapper readVariableSafe(String name, Object t) {
		Object val = scope.get(name, scope);
		if (val==null||val==Scriptable.NOT_FOUND) return new JavaScriptValue(t);
		return new JavaScriptValue(val);
	}
	
	
	
	@Override public void evaluateCode(String code, String name) {
		cx.evaluateString(scope, code, name, 1, null);
	}
	
	
	
	private void insertObject(Object obj, String name) {
		Object wrappedObj = Context.javaToJS(obj, scope);
		ScriptableObject.defineProperty(scope, name, wrappedObj, ScriptableObject.READONLY|ScriptableObject.PERMANENT);
	}
	
	

	@Override
	public ObjectWrapper callFunction(String name, String... args) throws IOException {
		Object func = scope.get(name, scope);
		if (func instanceof Function f) return new JavaScriptValue(f.call(cx, scope, scope, args));
		else throw new IOException(name + " is not a function or is not defined!");
	}
	
	@Override
	public ObjectWrapper callFunctionSafe(String name, Object defualt, String... args) throws IOException {
		Object func = scope.get(name, scope);
		if (func==null||func==Scriptable.NOT_FOUND) return new JavaScriptValue(defualt);
		else if (func instanceof Function f) return new JavaScriptValue(f.call(cx, scope, scope, args));
		else throw new IOException(name + " is not a function!");
	}
	
	
	
	@Override public void close() throws IOException {cx.close();}
	

	public static class JavaScriptIO {
		public void log  (Object str) {Hudder.log  (str);}
		public void warn (Object str) {Hudder.warn (str);}
		public void error(Object str) {Hudder.error(str);}
		public void debug(Object str) {Hudder.debug(str);}
		public void alert(Object str) {Hudder.alert(str);}
		public void showToast(String title, String content) {
			Hudder.showToast(Text.literal(title).formatted(Formatting.BOLD), Text.literal(content));
		}
	}
	
	

	@Override public CompileException processException(Exception e) {
		if (e instanceof RhinoException ex) {
			String msg = "\u00A74"+ex.getMessage()
					+"\n\u00A7bat Line "+ex.lineNumber()+" at col "+ex.columnNumber();
			return new CompileException(msg,-1,-1,ex);
		}
		if (e instanceof CompileException ex) return ex;
		var ex = new WrappedException(e);
		String msg = "\u00A74"+e.getMessage()
				+"\n\u00A7bat Line "+ex.lineNumber()+" at col "+ex.columnNumber();
		return new CompileException(msg,-1,-1,ex);
	}
	
	
	
	private class JavaScriptValue implements ObjectWrapper {
		private Object value;
		private JavaScriptValue(Object value) {
			this.value=value;
			if (value instanceof NativeJavaObject o) {this.value = o.unwrap();}
		}

		@Override public Object get() throws CompileException {return value;}
		
		@Override public String asString() {return Context.toString(value);}
		@Override public double asDouble() {return Context.toNumber(value);}
		@Override public boolean asBoolean() {return Context.toBoolean(value);}
		@Override public Object[] asArray() {return ((NativeArray) value).toArray();}
		
		@Override public String toString() {return asString();}
	}
	
}