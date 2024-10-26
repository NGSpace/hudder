package io.github.ngspace.hudder.compilers.utils;

import java.io.Closeable;
import java.io.IOException;

public interface IScriptingLanguageEngine extends Closeable {
	
	public void bindFunction(ScriptFunction function, String... names);
	public void bindConsumer(ScriptConsumer consumer, String... names);
	
	public void evaluateCode(String code, String name);
	
	public Object callFunction(String name, String... args) throws IOException;
	public Object callFunctionSafe(String name, Object defualt, String... args) throws IOException;
	
	public Object readVariable(String name);
	public Object readVariableSafe(String name, Object t);
	
	public CompileException processException(Exception e);
	
	public static interface ScriptFunction {public Object exec(Object... args) throws CompileException;}
	public static interface ScriptConsumer {public void   exec(Object... args) throws CompileException;}
}
