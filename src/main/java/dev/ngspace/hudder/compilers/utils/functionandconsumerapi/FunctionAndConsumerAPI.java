package dev.ngspace.hudder.compilers.utils.functionandconsumerapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.abstractions.ATextCompiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.utils.ObjectWrapper;
import dev.ngspace.hudder.utils.ValueGetter;
import dev.ngspace.hudder.variables.advanced.ComponentsData;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

/**
 * This is my attempt at unifying the Hudder and JavaScript compilers.<br>
 * 
 * Don't use this unless you know what you are doing.
 */
public class FunctionAndConsumerAPI {
	
	static FunctionAndConsumerAPI instance = new FunctionAndConsumerAPI();
	
	HashMap<BindableFunction, String[]> functions = new HashMap<BindableFunction, String[]>();
	HashMap<BindableConsumer, String[]> consumers = new HashMap<BindableConsumer, String[]>();
	
	List<Binder> binders = new ArrayList<Binder>();
	
	
	
	/**
	 * Applies registered consumers and functions to the provided binder as well as any that are added afterwards.
	 * @param binder
	 */
	public void applyFunctionsAndConsumers(Binder binder) {
		for (var cons : consumers.entrySet())
			binder.bindConsumer(cons.getKey(), cons.getValue());
		for (var func : functions.entrySet()) {
			binder.bindFunction(func.getKey(), func.getValue());
		}
		binders.add(binder);
	}
	
	
	
	public void registerFunction(BindableFunction func, String... names) {
		for (var binder : binders)
			binder.bindFunction(func, names);
		functions.put(func, names);
	}
	
	public void registerUnsafeFunction(BindableFunction func, String... names) {
		registerFunction((m,c,a)->{
			if (!Hudder.config.unsafeoperations())
				throw new CompileException("Called unsafe function with unsafe operations disabled!");
			return func.invoke(m,c,a);
		}, names);
	}



	public void registerConsumer(BindableConsumer cons, String... names) {
		for (var binder : binders) 
			binder.bindConsumer(cons, names);
		consumers.put(cons, names);
	}
	
	public void registerUnsafeConsumer(BindableConsumer cons, String... names) {
		registerConsumer((m,c,a)->{
			if (!Hudder.config.unsafeoperations())
				throw new CompileException("Called unsafe method with unsafe operations disabled!");
			cons.invoke(m,c,a);
		}, names);
	}
	
	

	@FunctionalInterface public interface BindableFunction {
		public Object invoke(IUIElementManager man, ATextCompiler comp, ObjectWrapper... args) throws CompileException;
	}
	@FunctionalInterface public interface BindableConsumer {
		public void invoke(IUIElementManager man, ATextCompiler comp, ObjectWrapper... args) throws CompileException;
	}

	
	
	public interface Binder {
		public void bindConsumer(BindableConsumer cons, String... names);
		public void bindFunction(BindableFunction cons, String... names);
	}
	
	
	
	public static FunctionAndConsumerAPI getInstance() {return instance;}
	

	public static class TranslatedItemStack implements ValueGetter {
		public String name;
		public int count;
		public int maxcount;
		public int durability;
		public int maxdurability;
		public String identifier;
		private DataComponentMap components;
		private ItemStack item;
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
		@Override public Object get(String component) {
			return ComponentsData.getObject(component, components, item);
		}
	}
}
