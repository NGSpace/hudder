package dev.ngspace.hudder.compilers.utils.functionandconsumerapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.ngspace.hudder.compilers.abstractions.ATextCompiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.data_management.ComponentsData;
import dev.ngspace.hudder.utils.ObjectWrapper;
import dev.ngspace.hudder.utils.ValueGetter;
import net.minecraft.core.component.DataComponentMap;
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



	public void registerConsumer(BindableConsumer func, String... names) {
		for (var binder : binders) 
			binder.bindConsumer(func, names);
		consumers.put(func, names);
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
		private DataComponentMap components;
		public TranslatedItemStack(ItemStack stack) {
			name = stack.getDisplayName().getString();
			count = stack.getCount();
			maxcount = stack.getMaxStackSize();
			durability = stack.getMaxDamage()-stack.getDamageValue();
			maxdurability = stack.getMaxDamage();
			components = stack.getComponents();
		}
		@Override public String toString() {
			return "{name:\"" + name + "\", count:" + count + ", maxcount: " + maxcount + ", durability: " + durability
					+ ", maxdurability: " + maxdurability + "}";
		}
		@Override public Object get(String component) {
			return ComponentsData.getObject(component, components);
		}
	}
}
