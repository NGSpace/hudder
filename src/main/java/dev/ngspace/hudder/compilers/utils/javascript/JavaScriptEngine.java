package dev.ngspace.hudder.compilers.utils.javascript;

import java.io.IOException;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.RhinoTextPosGetter;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrappedException;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.functionsandconsumers.IUIElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.PositionedBinder;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.abstractions.IScriptingLanguageEngine;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ObjectWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class JavaScriptEngine implements IScriptingLanguageEngine, PositionedBinder {

	protected static Minecraft mc = Minecraft.getInstance();
	
	private final ContextFactory contextFactory = new ContextFactory() {
		@Override protected Context makeContext() {
			Context context = super.makeContext();
			context.setWrapFactory(new HudderJavaScriptWrapFactory());
			context.setInterpretedMode(false);
			return context;
		}
	};
	
	private final Context cx;
	ScriptableObject scope;
	boolean closed;
	private AHudCompiler<?> compiler;
	private IUIElementManager elms;
	private HudderConfig config;
	
	public JavaScriptEngine(IUIElementManager elms, AHudCompiler<?> compiler, HudderConfig config) {
		this.compiler = compiler;
		this.elms = elms;
		this.config = config;
		cx = contextFactory.enterContext();
		try {
			scope = cx.initSafeStandardObjects();
		} finally {
			Context.exit();
		}
		
		var JavaScriptIO = new JavaScriptIO();
		
		insertObject(JavaScriptIO, "console");
		insertObject(JavaScriptIO, "hudder" );
	}
	
	private synchronized <T> T withContext(ContextAction<T> action) {
		if (closed) throw new IllegalStateException("JavaScript engine is closed");
		Context context = contextFactory.enterContext(cx);
		try {
			return action.run(context);
		} finally {
			Context.exit();
		}
	}

	@Override public void bindFunction(ScriptFunction function, String... names) {
		withContext(_ -> {
	        Function func = new BaseFunction() {
	            private static final long serialVersionUID = 1L;
			@Override public Object call(Context con, Scriptable scope, Scriptable thisObj, Object[] args) {
				try {
					ObjectWrapper[] vals = new ObjectWrapper[args.length];
					for (int i = 0;i<args.length;i++) {
						vals[i] = new JavaScriptValue(args[i]);
					}
					
					return con.getWrapFactory().wrap(con, scope, function.exec(
							RhinoTextPosGetter.getPosition(), vals), (Class<?>) null);
				} catch (Exception e) {
					throw new WrappedException(e);
				}
			}
	        };
	        for (String name : names) scope.put(name, scope, func);
	        return null;
		});
	}
	@Override public void bindConsumer(ScriptConsumer consumer, String... names) {
		bindFunction((p,e)->{consumer.exec(p,e);return Undefined.instance;},names);
	}
	
	
	
	@Override public ObjectWrapper readVariable(String name) {
		return withContext(_ -> {
			Object val = scope.get(name, scope);
			if (val==Scriptable.NOT_FOUND) return null;
			return new JavaScriptValue(val);
		});
	}
	@Override public ObjectWrapper readVariableSafe(String name, Object t) {
		return withContext(_ -> {
			Object val = scope.get(name, scope);
			if (val==null||val==Scriptable.NOT_FOUND) return new JavaScriptValue(t);
			return new JavaScriptValue(val);
		});
	}
	
	
	
	@Override public void evaluateCode(String code, String name) {
		withContext(context -> {
			context.evaluateString(scope, code, name, 1, null);
			return null;
		});
	}
	
	
	
	private void insertObject(Object obj, String name) {
		withContext(context -> {
			Object wrappedObj = Context.javaToJS(obj, scope, context);
			ScriptableObject.defineProperty(scope, name, wrappedObj, ScriptableObject.READONLY|ScriptableObject.PERMANENT);
			return null;
		});
	}
	
	

	@Override
	public synchronized ObjectWrapper callFunction(String name, String... args) throws IOException {
		Object func = withContext(_ -> scope.get(name, scope));
		if (func instanceof Function f) return withContext(context -> new JavaScriptValue(f.call(context, scope, scope, args)));
		else throw new IOException(name + " is not a function or is not defined!");
	}
	
	@Override
	public synchronized ObjectWrapper callFunctionSafe(String name, Object defualt, String... args) throws IOException {
		Object func = withContext(_ -> scope.get(name, scope));
		if (func==null||func==Scriptable.NOT_FOUND) return new JavaScriptValue(defualt);
		else if (func instanceof Function f) return withContext(context -> new JavaScriptValue(f.call(context, scope, scope, args)));
		else throw new IOException(name + " is not a function!");
	}
	
	
	
	@Override public synchronized void close() throws IOException {
		closed = true;
		scope = null;
		if (FunctionAndConsumerAPI.getInstance().containsBinder(this))
			FunctionAndConsumerAPI.getInstance().removeBinder(this);
	}
	
	

	@Override public ExecutionException processException(Exception e) {
		if (e instanceof RhinoException ex) {
			String msg = ex instanceof WrappedException wex ?
					wex.getWrappedException().getMessage() :ex.details();
			return new ExecutionException(msg,ex.lineNumber()-1,ex.columnNumber(),ex);
		}
		if (e instanceof ExecutionException ex) return ex;
		var ex = new WrappedException(e);
		return new ExecutionException(e.getMessage(),ex.lineNumber()-1,ex.columnNumber(),e);
	}
	
	

	@Override public CompileException processCompileException(Exception e) {
		if (e instanceof RhinoException ex) {
			String msg = ex instanceof WrappedException wex ?
					wex.getWrappedException().getMessage() :ex.details();
			return new CompileException(msg,ex.lineNumber()-1,ex.columnNumber(),ex);
		}
		if (e instanceof CompileException ex) return ex;
		var ex = new WrappedException(e);
		return new CompileException(e.getMessage(),ex.lineNumber()-1,ex.columnNumber(),e);
	}
	

	public static class JavaScriptIO {
		public void log  (Object str) {Hudder.log  (str);}
		public void warn (Object str) {Hudder.warn (str);}
		public void error(Object str) {Hudder.error(str);}
		public void debug(Object str) {Hudder.debug(str);}
		public void alert(Object str) {Hudder.alert(str);}
		public void showToast(String title, String content) {
			Hudder.showToast(Component.literal(title).withStyle(ChatFormatting.BOLD), Component.literal(content));
		}
	}
	
	
	
	private class JavaScriptValue implements ObjectWrapper {
		private Object value;
		private JavaScriptValue(Object value) {
			this.value=value;
			if (value instanceof NativeJavaObject o) {this.value = o.unwrap();}
		}

		@Override public Object get() throws ExecutionException {return value==Undefined.instance?null:value;}
		
		@Override public String asString() {return withContext(_ -> Context.toString(value));}
		@Override public double asDouble() {return withContext(_ -> Context.toNumber(value));}
		@Override public boolean asBoolean() {return withContext(_ -> Context.toBoolean(value));}
		@Override public Object[] asArray() {return withContext(_ -> ((NativeArray) value).toArray());}
		
		@Override public String toString() {return withContext(_ -> Context.toString(value));}

		@Override public <T> T asType(Class<T> clazz) throws ExecutionException {return clazz.cast(get());}
	}
	
	@Override
	public void bindFunction(BindablePositionedFunction c, String... n) {
		bindFunction((p,e)->c.invoke(elms, compiler, p, config, e), n);
	}
	
	@Override
	public void bindConsumer(BindablePositionedConsumer c, String... n) {
		bindConsumer((p,e)->c.invoke(elms, compiler, p, config, e), n);
	}
	
}
