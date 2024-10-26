package io.github.ngspace.hudder.compilers.utils;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class JavaScriptEngineWrapper implements IScriptingLanguageEngine {
	
	protected static MinecraftClient mc = MinecraftClient.getInstance();
	
	
	Context cx;
	ScriptableObject scope;
	public JavaScriptEngineWrapper() {
        cx = Context.enter();
        cx.setLanguageVersion(Context.VERSION_ES6);//For some reason this is not the default
        
        scope = cx.initSafeStandardObjects();
        
		insertObject(new JavaScriptIO(), "console");
		insertObject(new JavaScriptIO(), "hudder" );
		
		var JavaScriptIO = new JavaScriptIO();
		
		bindConsumer( s->JavaScriptIO.log(s[0]), "log");
		bindConsumer( s->JavaScriptIO.warn(s[0]), "warn");
		bindConsumer( s->JavaScriptIO.error(s[0]), "error");
		bindConsumer( s->JavaScriptIO.alert(s[0]), "alert");
	}
	
	@Override
	public void bindFunction(ScriptFunction function, String... names) {
        Function func = new BaseFunction() {
            private static final long serialVersionUID = 1L;
			@Override public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				try {
					for (int i = 0;i<args.length;i++)
						if (args[i] instanceof NativeJavaObject o)
							args[i] = o.unwrap();//Make sure the function gets the object and not a wrapper.
					return function.exec(args);
				} catch (Exception e) {
					throw new WrappedException(e);
				}
            }
        };
        for (String name : names) scope.put(name, scope, func);
	}
	@Override
	public void bindConsumer(ScriptConsumer consumer, String... names) {
        Function func = new BaseFunction() {
            private static final long serialVersionUID = 1L;
			@Override public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				try {
					consumer.exec(args);
					return Undefined.instance;
				} catch (Exception e) {
					throw new WrappedException(e);
				}
            }
        };
        for (String name : names) scope.put(name, scope, func);
	}
	
	@Override
	public Object readVariable(String name) {
		Object val = scope.get(name, scope);
		if (val==Scriptable.NOT_FOUND) return null;
		return val;
	}
	@Override
	public Object readVariableSafe(String name, Object t) {
		Object val = scope.get(name, scope);
		if (val==null||val==Scriptable.NOT_FOUND) return t;
		return val;
	}
	
	@Override
	public void evaluateCode(String code, String name) {
		cx.evaluateString(scope, code, name, 1, null);
	}
	
	private void insertObject(Object obj, String name) {
		Object wrappedObj = Context.javaToJS(obj, scope);
		ScriptableObject.putProperty(scope, name, wrappedObj);
	}

	@Override
	public Object callFunction(String name, String... args) throws IOException {
		Object func = scope.get(name, scope);
		if (func instanceof Function f) return f.call(cx, scope, scope, args);
		else throw new IOException(name + " is not a function or is not defined!");
	}
	
	@Override
	public Object callFunctionSafe(String name, Object defualt, String... args) throws IOException {
		Object func = scope.get(name, scope);
		if (func==null||func==Scriptable.NOT_FOUND) return defualt;
		else if (func instanceof Function f) return f.call(cx, scope, scope, args);
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
			Hudder.showToast(mc,Text.literal(title).formatted(Formatting.BOLD), Text.literal(content));
		}
	}

	@Override
	public CompileException processException(Exception e) {
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
}
