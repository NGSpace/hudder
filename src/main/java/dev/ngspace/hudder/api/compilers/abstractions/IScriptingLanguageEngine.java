package dev.ngspace.hudder.api.compilers.abstractions;

import java.io.Closeable;
import java.io.IOException;

import dev.ngspace.hudder.api.compilers.TextPos;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ObjectWrapper;

/**
 * Defines a common interface for scripting-language engines used to evaluate
 * code, expose Java-backed functions and consumers, call script functions, and
 * access script variables.
 * <p>
 * Implementations are {@link Closeable} and should release any resources held
 * by the underlying scripting engine when closed.
 * </p>
 */
public interface IScriptingLanguageEngine extends Closeable {
	
	/**
	 * Binds a script-accessible function under one or more names.
	 * <p>
	 * Calling any of the supplied names invokes the provided
	 * {@link ScriptFunction} and returns its result.
	 * </p>
	 *
	 * @param function the function to invoke
	 * @param names the names under which the function will be available
	 */
	public void bindFunction(ScriptFunction function, String... names);
	
	/**
	 * Binds a script-accessible consumer under one or more names.
	 * <p>
	 * Calling any of the supplied names invokes the provided
	 * {@link ScriptConsumer}.
	 * </p>
	 *
	 * @param consumer the consumer to invoke
	 * @param names the names under which the consumer will be available
	 */
	public void bindConsumer(ScriptConsumer consumer, String... names);
	
	
	
	/**
	 * Processes, compiles, or interprets the provided source code using this
	 * scripting engine.
	 *
	 * @param code the source code to evaluate
	 * @param filename the name of the source file, used to provide source context
	 *        such as debugging information
	 */
	public void evaluateCode(String code, String filename);
	
	
	
	/**
	 * Calls a script function with the specified name and arguments.
	 *
	 * @param name the name of the function to call
	 * @param args the arguments to pass to the function
	 * @return the value returned by the function
	 * @throws IOException if an I/O error occurs while calling the function
	 */
	public Object callFunction(String name, String... args) throws IOException;

	/**
	 * Calls a script function with the specified name, returning a default value
	 * when the implementation cannot produce a result.
	 *
	 * @param name the name of the function to call
	 * @param defualt the default value to return
	 * @param args the arguments to pass to the function
	 * @return the value returned by the function, or the supplied default value
	 *         when applicable
	 * @throws IOException if an I/O error occurs while calling the function
	 */
	public Object callFunctionSafe(String name, Object defualt, String... args) throws IOException;
	
	
	
	/**
	 * Reads a variable from the scripting environment.
	 *
	 * @param name the name of the variable to read
	 * @return a wrapper containing the variable's value
	 */
	public ObjectWrapper readVariable(String name);

	/**
	 * Reads a variable from the scripting environment, using the supplied value
	 * as a fallback when the variable cannot be read.
	 *
	 * @param name the name of the variable to read
	 * @param t the fallback value
	 * @return a wrapper containing the variable's value or the fallback value
	 */
	public ObjectWrapper readVariableSafe(String name, Object t);
	
	
	
	/**
	 * Converts an exception produced by the scripting engine into an execution
	 * exception used by Hudder.
	 *
	 * @param e the exception to process
	 * @return the corresponding execution exception
	 */
	public ExecutionException processException(Exception e);

	/**
	 * Converts an exception produced while processing source code into a compile
	 * exception used by Hudder.
	 *
	 * @param e the exception to process
	 * @return the corresponding compile exception
	 */
	public CompileException processCompileException(Exception e);
	
	
	
	/**
	 * Represents a Java-backed function that can be invoked by a scripting
	 * language engine.
	 */
	public static interface ScriptFunction {
		/**
		 * Executes the function with the supplied arguments.
		 *
		 * @param args the arguments supplied by the script
		 * @param pos the position of the call
		 * @return the value returned to the script
		 * @throws ExecutionException if the function cannot be executed
		 */
		public Object exec(TextPos pos, ObjectWrapper... args) throws ExecutionException;
	}

	/**
	 * Represents a Java-backed consumer that can be invoked by a scripting
	 * language engine.
	 */
	public static interface ScriptConsumer {
		/**
		 * Executes the consumer with the supplied arguments.
		 *
		 * @param args the arguments supplied by the script
		 * @param pos the position of the call
		 * @throws ExecutionException if the consumer cannot be executed
		 */
		public void exec(TextPos pos, ObjectWrapper... args) throws ExecutionException;
	}
	
}