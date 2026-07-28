package dev.ngspace.hudder.hudderv3;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableFunction;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ImplObjectWrapper;
import dev.ngspace.hudder.utils.ObjectWrapper;

public class HudderV3Helper {
	public static Map<String, BindableFunction> api_functions = new HashMap<String, BindableFunction>();
	public static Map<String, BindableConsumer> api_consumers = new HashMap<String, BindableConsumer>();

	private HudderV3Helper() {}
	
	public static boolean compare(Object val1, Object val2, String comparisonOperator) throws ExecutionException {
		if (val1==null||val2==null) {
			if (comparisonOperator.equals("=="))
				return val1==val2;
			else if (comparisonOperator.equals("!="))
				return val1!=val2;
			else throw new ExecutionException("Can not compare null values using the "+comparisonOperator+" operator.",
					-1, -1);
		}
		boolean areNums = false;
		double dou1 = 0;
		double dou2 = 0;
		if (val1 instanceof Number num1) {
			dou1 = num1.doubleValue();
			if (val2 instanceof Number num2) {
				dou2 = num2.doubleValue();
				areNums = true;
			}
		}
		return switch (comparisonOperator) {
			case "==" -> areNums ? dou1==dou2 :  Objects.equals(val1, val2);
			case "!=" -> areNums ? dou1!=dou2 : !Objects.equals(val1, val2);
			case ">=" -> dou1>=dou2;
			case "<=" -> dou1<=dou2;
			case ">"  -> dou1> dou2;
			case "<"  -> dou1< dou2;
			default -> throw new IllegalArgumentException("Unknown comparasion operator: " + comparisonOperator);
		};
	}
	
	public static boolean hasApiFunction(String name) {
		return api_functions.containsKey(name.toLowerCase().trim());
	}
	
	
	public static Object callApiFunction(String name, ArrayElementManager uiManager,
			AVarTextCompiler compiler, Object... values) throws ExecutionException {
		return api_functions.get(name.toLowerCase().trim()).invoke(uiManager, compiler,
				Stream.of(values).map(ImplObjectWrapper::new).toList().toArray(new ObjectWrapper[0]));
	}
	
	public static boolean hasApiConsumer(String name) {
		return api_consumers.containsKey(name.toLowerCase().trim());
	}
	
	public static void callApiConsumer(String name, ArrayElementManager uiManager,
			AVarTextCompiler compiler, Object... values) throws ExecutionException {
		api_consumers.get(name.toLowerCase().trim()).invoke(uiManager, compiler,
				ImplObjectWrapper.fromArray(values));
	}
}
