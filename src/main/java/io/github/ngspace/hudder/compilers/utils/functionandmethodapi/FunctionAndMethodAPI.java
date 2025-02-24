package io.github.ngspace.hudder.compilers.utils.functionandmethodapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.data_management.BooleanData;
import io.github.ngspace.hudder.data_management.ComponentsData;
import io.github.ngspace.hudder.data_management.NumberData;
import io.github.ngspace.hudder.data_management.ObjectDataAPI;
import io.github.ngspace.hudder.data_management.StringData;
import io.github.ngspace.hudder.main.HudCompilationManager;
import io.github.ngspace.hudder.utils.HudFileUtils;
import io.github.ngspace.hudder.utils.ObjectWrapper;
import io.github.ngspace.hudder.utils.ValueGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;

/**
 * This is my attempt at unifying the Hudder and JavaScript compilers.<br>
 * 
 * Don't use this unless you know what you are doing.
 */
public class FunctionAndMethodAPI {private FunctionAndMethodAPI() {}
	
	static FunctionAndMethodAPI instance = new FunctionAndMethodAPI();
	
	HashMap<BindableFunction, String> functions = new HashMap<BindableFunction, String>();
	HashMap<BindableConsumer, String> consumers = new HashMap<BindableConsumer, String>();
	
	List<FunctionBinder> functions_binders = new ArrayList<FunctionBinder>();
	List<ConsumerBinder> consumers_binders = new ArrayList<ConsumerBinder>();
	
	public void applyConsumers(ConsumerBinder binder) {
		for (var cons : consumers.entrySet())
			binder.bindConsumer(cons.getKey(), cons.getValue());
		
		consumers_binders.add(binder);
	}
	
	
	
	public void applyFunctions(FunctionBinder binder) {
		for (var func : functions.entrySet())
			binder.bindFunction(func.getKey(), func.getValue());
		functions_binders.add(binder);
	}
	
	
	
	public void registerFunction(BindableFunction func, String... names) {
		for (String name : names) {
			for (var binder : functions_binders)
				binder.bindFunction(func, names);
			functions.put(func, name);
		}
	}



	public void registerConsumer(BindableConsumer func, String... names) {
		for (String name : names) {
			for (var binder : consumers_binders) 
				binder.bindConsumer(func, names);
			consumers.put(func, name);
		}
	}

	
	
	@FunctionalInterface public interface BindableConsumer {
		public void invoke(IElementManager man, ATextCompiler comp, ObjectWrapper... args) throws CompileException;
	}
	@FunctionalInterface public interface ConsumerBinder {
		public void bindConsumer(BindableConsumer cons, String... names);
	}
	
	

	@FunctionalInterface public interface BindableFunction {
		public Object invoke(IElementManager man, ATextCompiler comp, ObjectWrapper... args) throws CompileException;
	}
	@FunctionalInterface public interface FunctionBinder {
		public void bindFunction(BindableFunction cons, String... names);
	}
	
	public static FunctionAndMethodAPI getInstance() {return instance;}
	

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
