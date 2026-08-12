package dev.ngspace.hudder.api.functionsandconsumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.PositionedBinder;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
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
	
	HashMap<BindablePositionedFunction, String[]> functions = new HashMap<BindablePositionedFunction, String[]>();
	HashMap<BindablePositionedConsumer, String[]> consumers = new HashMap<BindablePositionedConsumer, String[]>();
	
	List<PositionedBinder> binders = new ArrayList<PositionedBinder>();
	
	

	/**
	 * @deprecated use BindablePositionedConsumer
	 */
	@Deprecated(since = "10.3.0", forRemoval = true)
	public void applyFunctionsAndConsumers(Binder binder) {
		for (var cons : consumers.entrySet())
			binder.bindConsumer(cons.getKey(), cons.getValue());
		for (var func : functions.entrySet())
			binder.bindFunction(func.getKey(), func.getValue());
		binders.add(binder);
	}
	/**
	 * Applies all currently registered consumers and functions to the specified
	 * binder and registers it to receive future additions.
	 *
	 * @param binder the binder to which functions and consumers will be applied
	 */
	public void applyFunctionsAndConsumers(PositionedBinder binder) {
		for (var cons : consumers.entrySet())
			binder.bindConsumer(cons.getKey(), cons.getValue());
		for (var func : functions.entrySet())
			binder.bindFunction(func.getKey(), func.getValue());
		binders.add(binder);
	}
	
	
	/**
	 * @deprecated use {@link #registerFunction(BindablePositionedFunction, String...)}
	 */
	@Deprecated(since = "10.3.0", forRemoval = true)
	public void registerFunction(BindableFunction func, String... names) {
		for (var binder : binders)
			binder.bindFunction(func, names);
		functions.put(func, names);
	}
	/**
	 * @deprecated use {@link #registerUnsafeFunction(BindablePositionedFunction, String...)}
	 */
	@Deprecated(since = "10.3.0", forRemoval = true)
	public void registerUnsafeFunction(BindableFunction func, String... names) {
		registerFunction((m,c,a)->{
			if (!Hudder.config.unsafeoperations())
				throw new SecurityException("Called unsafe function with unsafe operations disabled!");
			return func.invoke(m,c,a);
		}, names);
	}
	

	/**
	 * @deprecated use {@link #registerDeprecatedFunction(String, BindablePositionedFunction, String...)}
	 */
	@Deprecated(since = "10.3.0", forRemoval = true)
	public void registerDeprecatedFunction(String warning, BindableFunction func, String... names) {
		registerFunction(new DeprecatedFunciton(warning, func, names), names);
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
	public void registerFunction(BindablePositionedFunction func, String... names) {
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
	public void registerUnsafeFunction(BindablePositionedFunction func, String... names) {
		registerFunction((m,c,p,a)->{
			if (!Hudder.config.unsafeoperations())
				throw new SecurityException("Called unsafe function with unsafe operations disabled!");
			return func.invoke(m,c,p,a);
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
	public void registerDeprecatedFunction(String warning, BindablePositionedFunction func, String... names) {
		registerFunction(new DeprecatedFunciton(warning, func, names), names);
	}
	/**
	 * @deprecated use {@link #registerConsumer(BindablePositionedConsumer, String...)}
	 */
	@Deprecated(since = "10.3.0", forRemoval = true)
	public void registerConsumer(BindableConsumer cons, String... names) {
		for (var binder : binders) 
			binder.bindConsumer(cons, names);
		consumers.put(cons, names);
	}

	/**
	 * @deprecated use {@link #registerUnsafeConsumer(BindablePositionedConsumer, String...)}
	 */
	@Deprecated(since = "10.3.0", forRemoval = true)
	public void registerUnsafeConsumer(BindableConsumer cons, String... names) {
		registerConsumer((m,c,a)->{
			if (!Hudder.config.unsafeoperations())
				throw new SecurityException("Called unsafe method with unsafe operations disabled!");
			cons.invoke(m,c,a);
		}, names);
	}

	/**
	 * @deprecated use {@link #registerDeprecatedConsumer(String, BindablePositionedConsumer, String...)}
	 */
	@Deprecated(since = "10.3.0", forRemoval = true)
	public void registerDeprecatedConsumer(String warning, BindableConsumer cons, String... names) {
		registerConsumer(new DeprecatedConsumer(warning, cons, names), names);
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
	public void registerConsumer(BindablePositionedConsumer cons, String... names) {
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
	public void registerUnsafeConsumer(BindablePositionedConsumer cons, String... names) {
		registerConsumer((m,c,p,a)->{
			if (!Hudder.config.unsafeoperations())
				throw new SecurityException("Called unsafe method with unsafe operations disabled!");
			cons.invoke(m,c,p,a);
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
	public void registerDeprecatedConsumer(String warning, BindablePositionedConsumer cons, String... names) {
		registerConsumer(new DeprecatedConsumer(warning, cons, names), names);
	}
	
	


	/**
	 * @deprecated use BindablePositionedFunction
	 */
	@Deprecated(since = "10.3.0", forRemoval = true)
	@FunctionalInterface public interface BindableFunction extends BindablePositionedFunction {
		@Deprecated(since = "10.3.0", forRemoval = true)
		public Object invoke(IUIElementManager man, AHudCompiler<?> comp, ObjectWrapper... args) throws ExecutionException;
		@Deprecated(since = "10.3.0", forRemoval = true)
		@Override
		default Object invoke(IUIElementManager man, AHudCompiler<?> comp, TextPos position, ObjectWrapper... args)
				throws ExecutionException {
			return invoke(man, comp, args);
		}
	}
	
	

	/**
	 * @deprecated use BindablePositionedConsumer
	 */
	@Deprecated(since = "10.3.0", forRemoval = true)
	@FunctionalInterface public interface BindableConsumer extends BindablePositionedConsumer {
		@Deprecated(since = "10.3.0", forRemoval = true)
		public void invoke(IUIElementManager man, AHudCompiler<?> comp, ObjectWrapper... args) throws ExecutionException;
		@Deprecated(since = "10.3.0", forRemoval = true)
		@Override
		default void invoke(IUIElementManager man, AHudCompiler<?> comp, TextPos position, ObjectWrapper... args)
				throws ExecutionException {
			invoke(man, comp, args);
		}
	}

	
	
	/**
	 * @deprecated use PositionedBinder
	 */
	@Deprecated(since = "10.3.0",forRemoval = true)
	public interface Binder extends PositionedBinder {
		/**
		 * Binds a consumer under one or more names.
		 *
		 * @param cons the consumer to bind
		 * @param names the names under which the consumer will be available
		 */
		@Deprecated(since = "10.3.0",forRemoval = true)
		public void bindConsumer(BindableConsumer cons, String... names);
		
		@Deprecated(since = "10.3.0",forRemoval = true)
		public default void bindConsumer(BindablePositionedConsumer cons, String... names) {
			if (cons instanceof BindableConsumer old)
				bindConsumer(old, names);
			else
				bindConsumer((i,j,k)->cons.invoke(i, j, new TextPos(-1, -1), k), names);
		}

		/**
		 * Binds a function under one or more names.
		 *
		 * @param cons the function to bind
		 * @param names the names under which the function will be available
		 */
		@Deprecated(since = "10.3.0",forRemoval = true)
		public void bindFunction(BindableFunction cons, String... names);
		
		@Deprecated(since = "10.3.0",forRemoval = true)
		public default void bindFunction(BindablePositionedFunction cons, String... names) {
			if (cons instanceof BindableFunction old)
				bindFunction(old, names);
			else
				bindFunction((i,j,k)->cons.invoke(i, j, new TextPos(-1, -1), k), names);
		}
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
