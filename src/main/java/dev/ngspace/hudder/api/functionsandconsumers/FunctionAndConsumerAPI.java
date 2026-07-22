package dev.ngspace.hudder.api.functionsandconsumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ObjectWrapper;
import dev.ngspace.hudder.utils.ValueGetter;
import dev.ngspace.hudder.variables.advanced.ComponentsData;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

/**
 * Provides a shared registry for functions and consumers that can be bound to
 * Hudder's supported compilers.
 * <p>
 * Registered functions and consumers are applied to existing binders
 * immediately. Binders added through {@link #applyFunctionsAndConsumers(Binder)}
 * also receive registrations made afterward.
 * </p>
 */
public class FunctionAndConsumerAPI {
	
	static FunctionAndConsumerAPI instance = new FunctionAndConsumerAPI();
	
	HashMap<BindableFunction, String[]> functions = new HashMap<BindableFunction, String[]>();
	HashMap<BindableConsumer, String[]> consumers = new HashMap<BindableConsumer, String[]>();
	
	List<Binder> binders = new ArrayList<Binder>();
	
	
	
	/**
	 * Applies all currently registered consumers and functions to the specified
	 * binder and registers it to receive future additions.
	 *
	 * @param binder the binder to which functions and consumers will be applied
	 */
	public void applyFunctionsAndConsumers(Binder binder) {
		for (var cons : consumers.entrySet())
			binder.bindConsumer(cons.getKey(), cons.getValue());
		for (var func : functions.entrySet())
			binder.bindFunction(func.getKey(), func.getValue());
		binders.add(binder);
	}
	
	
	/**
	 * Registers a function under one or more names.
	 * <p>
	 * The function is immediately applied to all previously registered binders.
	 * </p>
	 *
	 * @param func the function to register
	 * @param names the names under which the function will be bound
	 */
	public void registerFunction(BindableFunction func, String... names) {
		for (var binder : binders)
			binder.bindFunction(func, names);
		functions.put(func, names);
	}
	
	/**
	 * Registers a function that may only be invoked while unsafe operations are
	 * enabled in Hudder's configuration.
	 *
	 * @param func the unsafe function to register
	 * @param names the names under which the function will be bound
	 * @see #registerFunction(BindableFunction, String...)
	 */
	public void registerUnsafeFunction(BindableFunction func, String... names) {
		registerFunction((m,c,a)->{
			if (!Hudder.config.unsafeoperations())
				throw new SecurityException("Called unsafe function with unsafe operations disabled!");
			return func.invoke(m,c,a);
		}, names);
	}
	
	/**
	 * Registers a deprecated function that produces the specified warning when
	 * used.
	 *
	 * @param warning the deprecation warning associated with the function
	 * @param func the function to register
	 * @param names the names under which the function will be bound
	 * @see #registerFunction(BindableFunction, String...)
	 */
	public void registerDeprecatedFunction(String warning, BindableFunction func, String... names) {
		registerFunction(new DeprecatedFunciton(warning, func, names), names);
	}


	
	/**
	 * Registers a consumer under one or more names.
	 * <p>
	 * The consumer is immediately applied to all previously registered binders.
	 * </p>
	 *
	 * @param cons the consumer to register
	 * @param names the names under which the consumer will be bound
	 */
	public void registerConsumer(BindableConsumer cons, String... names) {
		for (var binder : binders) 
			binder.bindConsumer(cons, names);
		consumers.put(cons, names);
	}
	
	/**
	 * Registers a consumer that may only be invoked while unsafe operations are
	 * enabled in Hudder's configuration.
	 *
	 * @param cons the unsafe consumer to register
	 * @param names the names under which the consumer will be bound
	 * @see #registerConsumer(BindableConsumer, String...)
	 */
	public void registerUnsafeConsumer(BindableConsumer cons, String... names) {
		registerConsumer((m,c,a)->{
			if (!Hudder.config.unsafeoperations())
				throw new SecurityException("Called unsafe method with unsafe operations disabled!");
			cons.invoke(m,c,a);
		}, names);
	}
	
	/**
	 * Registers a deprecated consumer that produces the specified warning when
	 * used.
	 *
	 * @param warning the deprecation warning associated with the consumer
	 * @param cons the consumer to register
	 * @param names the names under which the consumer will be bound
	 * @see #registerConsumer(BindableConsumer, String...)
	 */
	public void registerDeprecatedConsumer(String warning, BindableConsumer cons, String... names) {
		registerConsumer(new DeprecatedConsumer(warning, cons, names), names);
	}
	
	

	/**
	 * Represents a function that can be exposed to a compiler through a
	 * {@link Binder}.
	 */
	@FunctionalInterface public interface BindableFunction {
		/**
		 * Invokes the function with the supplied execution context and arguments.
		 *
		 * @param man the UI element manager associated with the invocation
		 * @param comp the compiler performing the invocation
		 * @param args the arguments supplied to the function
		 * @return the value returned by the function
		 * @throws ExecutionException if the function cannot be executed
		 */
		public Object invoke(IUIElementManager man, AHudCompiler<?> comp, ObjectWrapper... args) throws ExecutionException;
	}

	/**
	 * Represents a value-consuming operation that can be exposed to a compiler
	 * through a {@link Binder}.
	 */
	@FunctionalInterface public interface BindableConsumer {
		/**
		 * Invokes the consumer with the supplied execution context and arguments.
		 *
		 * @param man the UI element manager associated with the invocation
		 * @param comp the compiler performing the invocation
		 * @param args the arguments supplied to the consumer
		 * @throws ExecutionException if the consumer cannot be executed
		 */
		public void invoke(IUIElementManager man, AHudCompiler<?> comp, ObjectWrapper... args) throws ExecutionException;
	}

	
	
	// Tranz Rightz
	/**
	 * Binds registered functions and consumers to a compiler or other execution
	 * environment.
	 */
	public interface Binder {
		/**
		 * Binds a consumer under one or more names.
		 *
		 * @param cons the consumer to bind
		 * @param names the names under which the consumer will be available
		 */
		public void bindConsumer(BindableConsumer cons, String... names);

		/**
		 * Binds a function under one or more names.
		 *
		 * @param cons the function to bind
		 * @param names the names under which the function will be available
		 */
		public void bindFunction(BindableFunction cons, String... names);
	}
	
	
	
	/**
	 * Returns the shared function and consumer API instance.
	 *
	 * @return the shared API instance
	 */
	public static FunctionAndConsumerAPI getInstance() {return instance;}
	

	public static class TranslatedItemStack implements ValueGetter {
		public String name;
		public int count;
		public int maxcount;
		public int durability;
		public int maxdurability;
		public String identifier;
		public DataComponentMap components;
		public ItemStack item;
		public TranslatedItemStack(ItemStack stack) {
			name = stack.getDisplayName().getString();
			count = stack.getCount();
			maxcount = stack.getMaxStackSize();
			durability = stack.getMaxDamage()-stack.getDamageValue();
			maxdurability = stack.getMaxDamage();
			components = stack.getComponents();
			identifier = BuiltInRegistries.ITEM.wrapAsHolder(stack.getItem()).getRegisteredName();
			item = stack;
		}
		@Override public String toString() {
			return "{name:\"" + name + "\", count:" + count + ", maxcount: " + maxcount + ", durability: " + durability
					+ ", maxdurability: " + maxdurability + ", identifier: " + identifier + "}";
		}
		@Override public Object get(String key) {
			
			return switch (key) {
				case "name": yield name;
				case "count": yield count;
				case "maxcount": yield maxcount;
				case "durability": yield durability;
				case "maxdurability": yield maxdurability;
				case "identifier": yield maxdurability;
				default: yield ComponentsData.getObject(key, components, item);
			};
		}
	}
}
