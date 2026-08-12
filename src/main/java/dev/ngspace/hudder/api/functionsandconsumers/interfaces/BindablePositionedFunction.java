package dev.ngspace.hudder.api.functionsandconsumers.interfaces;

import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.Binder;
import dev.ngspace.hudder.api.functionsandconsumers.IUIElementManager;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ObjectWrapper;

/**
 * Represents a function that can be exposed to a compiler through a
 * {@link Binder}.
 */
@FunctionalInterface public interface BindablePositionedFunction {
	/**
	 * Invokes the function with the supplied execution context and arguments.
	 *
	 * @param man the UI element manager associated with the invocation
	 * @param comp the compiler performing the invocation
	 * @param position the position the function is called from
	 * @param args the arguments supplied to the function
	 * @return the value returned by the function
	 * @throws ExecutionException if the function cannot be executed
	 */
	public Object invoke(IUIElementManager man, AHudCompiler<?> comp, TextPos position, ObjectWrapper... args)
			throws ExecutionException;
}