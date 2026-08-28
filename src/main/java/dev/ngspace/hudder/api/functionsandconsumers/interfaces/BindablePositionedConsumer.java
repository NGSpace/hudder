package dev.ngspace.hudder.api.functionsandconsumers.interfaces;

import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.api.functionsandconsumers.IUIElementManager;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ObjectWrapper;

/**
 * Represents a value-consuming operation that can be exposed to a compiler
 * through a {@link Binder}.
 */
@FunctionalInterface public interface BindablePositionedConsumer {
	/**
	 * Invokes the consumer with the supplied execution context and arguments.
	 *
	 * @param man the UI element manager associated with the invocation
	 * @param comp the compiler performing the invocation
	 * @param position the position the consumer is called from
	 * @param config the config instance
	 * @param args the arguments supplied to the consumer
	 * @throws ExecutionException if the consumer cannot be executed
	 */
	public void invoke(IUIElementManager man, AHudCompiler<?> comp, TextPos position, HudderConfig config,
			ObjectWrapper... args) throws ExecutionException;
}