package dev.ngspace.hudder.api.functionsandconsumers.interfaces;

// Tranz Rightz
/**
 * Binds registered functions and consumers to a compiler or other execution
 * environment.
 */
public interface PositionedBinder {
	/**
	 * Binds a consumer under one or more names.
	 *
	 * @param cons the consumer to bind
	 * @param names the names under which the consumer will be available
	 */
	public void bindConsumer(BindablePositionedConsumer cons, String... names);

	/**
	 * Binds a function under one or more names.
	 *
	 * @param cons the function to bind
	 * @param names the names under which the function will be available
	 */
	public void bindFunction(BindablePositionedFunction cons, String... names);
}