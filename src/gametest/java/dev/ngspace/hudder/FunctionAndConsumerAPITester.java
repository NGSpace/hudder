package dev.ngspace.hudder;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.PositionedBinder;
import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ImplObjectWrapper;

public class FunctionAndConsumerAPITester implements PositionedBinder {
	
	static HashMap<String, BindablePositionedConsumer> consumers = new HashMap<String, BindablePositionedConsumer>();
	static HashMap<String, BindablePositionedFunction> functions = new HashMap<String, BindablePositionedFunction>();
	private static FunctionAndConsumerAPITester instance;
	
	static {
		instance = new FunctionAndConsumerAPITester();
		FunctionAndConsumerAPI.getInstance().applyFunctionsAndConsumers(instance);
	}
	
	public static void runFunction(String function, Object... args) {
		testFunction(function, Hudder.config, Hudder.config.hudderV3Compiler, (_, _) -> true, args);
	}
	
	public static void testFunction(String function, BiFunction<Object, ArrayElementManager, Boolean> result_validator,
			Object... args) {
		testFunction(function, Hudder.config, Hudder.config.hudderV3Compiler, result_validator, args);
	}
	
	public static void testFunction(String function, HudderConfig config, AHudCompiler<?> comp,
			BiFunction<Object, ArrayElementManager, Boolean> result_validator, Object... args) {
		ArrayElementManager outputElementManager = new ArrayElementManager();
		try {
			Object val = functions.get(function).invoke(outputElementManager, comp, new TextPos(-1, -1), config,
					ImplObjectWrapper.fromArray(args));
			if (!result_validator.apply(val, outputElementManager)) {
				throw new AssertionError(function + " returned \"" + val + "\"");
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new AssertionError(function + " failed to execute");
		}
	}
	
	public static void runConsumer(String function, Object... args) {
		testConsumer(function, Hudder.config, Hudder.config.hudderV3Compiler, _ -> true, args);
	}
	
	public static void testConsumer(String function, Function<ArrayElementManager, Boolean> result_validator,
			Object... args) {
		testConsumer(function, Hudder.config, Hudder.config.hudderV3Compiler, result_validator, args);
	}
	
	public static void testConsumer(String function, HudderConfig config, AHudCompiler<?> comp,
			Function<ArrayElementManager, Boolean> result_validator, Object... args) {
		ArrayElementManager outputElementManager = new ArrayElementManager();
		
		try {
			consumers.get(function).invoke(outputElementManager, comp, new TextPos(-1, -1), config,
					ImplObjectWrapper.fromArray(args));
			if (!result_validator.apply(outputElementManager)) {
				throw new AssertionError(function + " failed to pass validator");
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new AssertionError(function + " failed to execute");
		}
	}
	
	@Override
	public void bindConsumer(BindablePositionedConsumer cons, String... names) {
		for (String name : names)
			consumers.put(name, cons);
	}
	
	@Override
	public void bindFunction(BindablePositionedFunction cons, String... names) {
		for (String name : names)
			functions.put(name, cons);
	}
}
