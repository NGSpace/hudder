package io.github.ngspace.hudder.compilers;

import java.io.Closeable;
import java.io.IOException;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrappedException;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class JavaScriptEngineWrapper implements Closeable {
	Context cx;
	boolean setClassShutter = true;
	ScriptableObject scope;
	public JavaScriptEngineWrapper() {
        cx = Context.enter();
        try {
//        	if (setClassShutter)
//        		cx.getClassShutterSetter().setClassShutter(clazz->clazz.startsWith(
//        				"io.github.ngspace.hudder.compilers.JavaScript"));
        } finally {
			setClassShutter = false;
		}
        scope = cx.initSafeStandardObjects();
        
        cx.setLanguageVersion(Context.VERSION_ES6);
        
		insertObject(new JavaScriptIO(), "console");
		insertObject(new JavaScriptIO(), "hudder" );
	}

	public void bindFunction(ScriptFunction function, String... names) {
        Function func = new BaseFunction() {
            private static final long serialVersionUID = 1L;
			@Override public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				try {
					for (int i = 0;i<args.length;i++) {
						if (args[i] instanceof NativeJavaObject o) {
							args[i] = o.unwrap();
						}
					}
					return function.exec(args);
				} catch (Exception e) {
					throw new WrappedException(e);
				}
            }
        };
        for (String name : names) scope.put(name, scope, func);
	}
	public void bindConsumer(ScriptConsumer function, String... names) {
        Function func = new BaseFunction() {
            private static final long serialVersionUID = 1L;
			@Override public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				try {
					function.exec(args);
					return Undefined.instance;
				} catch (Exception e) {
					throw new WrappedException(e);
				}
            }
        };
        for (String name : names) scope.put(name, scope, func);
	}

	public Object readVariable(String name) {
		Object val = scope.get(name, scope);
		if (val==Scriptable.NOT_FOUND) return null;
		return val;
	}

	public Object readVariableSafe(String name, Object t) {
		Object val = scope.get(name, scope);
		if (val==null||val==Scriptable.NOT_FOUND) return t;
		return val;
	}
	
	public void evaluateString(String code, String name) {
		cx.evaluateString(scope, code, name, 1, null);
	}
	
	private void insertObject(Object obj, String name) {
		Object wrappedObj = Context.javaToJS(obj, scope);
		ScriptableObject.putProperty(scope, name, wrappedObj);
	}

	public Object callFunction(String name, String... args) throws IOException {
		Object func = scope.get(name, scope);
		if (func instanceof Function f) return f.call(cx, scope, scope, args);
		else throw new IOException(name + " is not a function or is not defined!");
	}
	public Object callFunctionSafe(String name, Object defualt, String... args) throws IOException {
		Object func = scope.get(name, scope);
		if (func==null||func==Scriptable.NOT_FOUND) return defualt;
		else if (func instanceof Function f) return f.call(cx, scope, scope, args);
		else throw new IOException(name + " is not a function!");
	}
	
	@Override public void close() throws IOException {cx.close();}
	
	public static interface ScriptFunction {public Object exec(Object... args) throws CompileException;}
	public static interface ScriptConsumer {public void   exec(Object... args) throws CompileException;}
	

	public static class JavaScriptIO {
		public void log  (Object str) {Hudder.log  (str);}
		public void warn (Object str) {Hudder.warn (str);}
		public void error(Object str) {Hudder.error(str);}
		public void debug(Object str) {Hudder.debug(str);}
		public void alert(Object str) {Hudder.alert(str);}
		public void showToast(String title, String content) {
			Hudder.showToast(Hudder.ins,Text.literal(title).formatted(Formatting.BOLD), Text.literal(content));
		}
	}


	public CompileException processException(Exception e) {
		if (e instanceof RhinoException ex) {
			String msg = "\u00A74"+ex.getMessage()
					+"\n\u00A7bat Line "+ex.lineNumber()+" at col "+ex.columnNumber();
			return new CompileException(msg,-1,-1,ex);
		}
		if (e instanceof CompileException ex) return ex;
//		return new CompileException(e.getMessage(),-1,-1,e);
		var ex = new WrappedException(e);
		String msg = "\u00A74"+e.getMessage()
				+"\n\u00A7bat Line "+ex.lineNumber()+" at col "+ex.columnNumber();
		return new CompileException(msg,-1,-1,ex);
//		return processException();
	}
}
