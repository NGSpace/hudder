package dev.ngspace.hudder.compilers.abstractions;

import java.io.Closeable;
import java.io.IOException;

import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.utils.ObjectWrapper;

public interface IScriptingLanguageEngine extends Closeable {
	
	/**
	 * Adds a (most likely) global function that executes the provided ScriptFunction object when called using the
	 * provided names and returns the value that the ScriptFunction returns.
	 * @param function - the interface to be called once the function is called
	 * @param names - the names of the function
	 */
	public void bindFunction(ScriptFunction function, String... names);
	
	/**
	 * Adds a (most likely) global consumer that executes the provided ScriptConsumer object when called using the
	 * provided names.
	 * @param consumer - the interface to be called once the consumer is called
	 * @param names - the names of the consumer
	 */
	public void bindConsumer(ScriptConsumer consumer, String... names);
	
	
	
	/**
	 * Process, compile or interpret the provided code.
	 * @param code - the string representation of the code.
	 * @param filename - the name of the source file, useful for debugging.
	 */
	public void evaluateCode(String code, String filename);
	
	
	
	public Object callFunction(String name, String... args) throws IOException;
	public Object callFunctionSafe(String name, Object defualt, String... args) throws IOException;
	
	
	
	public ObjectWrapper readVariable(String name);
	public ObjectWrapper readVariableSafe(String name, Object t);
	
	
	
	public CompileException processException(Exception e);
	
	
	
	public static interface ScriptFunction {public Object exec(ObjectWrapper... args) throws CompileException;}
	public static interface ScriptConsumer {public void   exec(ObjectWrapper... args) throws CompileException;}
}
