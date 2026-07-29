package dev.ngspace.hudder.v2runtime.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableFunction;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.Binder;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.functions.V2FunctionHandler.RangedIterator;

public class HudderFunctions {
	private HudderFunctions() {/* */}

	public static void bindAllAPIFunctions(Binder binder) {
		
		//Type casting
		
		bindFunction(binder,(_,_,args) -> {
			Object value = args[0].get();
			
			if (value==null) throw new ExecutionException("Value of variable is null!", -1, -1);
			
			switch (value) {
				case Number num:
					return num.intValue();
				case String str:
					return Double.parseDouble(str);
				case Boolean bool:
					return Boolean.TRUE.equals(bool)?1d:0d;
				case Character c:
					return ((int)c);
				default:
					return Double.parseDouble(value.toString());
			}
		}, 1, "int", "num", "number", "double");
		bindFunction(binder, (_,_,args) -> {

			Object value = args[0].get();
			
			if (args.length==2&&(boolean) args[1].get()&&value instanceof Number num)
				return cleanDouble(num.doubleValue());
			if (value instanceof List<?> s) {
				StringBuilder b = new StringBuilder();
				for (var v : s) b.append(v);
				return b.toString();
			}
			return value.toString();
		}, 1, 2, "str", "string");
		bindFunction(binder, (_,_,args) -> {
			var lst = new ArrayList<Object>();
			Object o = args[0].get();
			if (o instanceof String str) {
				for (char c : str.toCharArray()) lst.add(c);
			} else if (o instanceof Number b) {
				Object value = 0;
				if (args.length>1) value = args[1].get();
				for (int i = 0;i<b.intValue();i++) {
					lst.add(value);
				}
			}
			return lst;
		}, 1, 2, "array");
		bindFunction(binder, (_,_,args) -> (char)(args[0].asDouble()), 1, "char");
		bindFunction(binder, (_,_,args) -> Integer.toBinaryString((int) args[0].asDouble()), 1, "toBinaryString");
		
		//Math
		
		bindFunction(binder,new RngV2Function(), 2, 3, "rng", "random");

		bindFunction(binder, (_,_,args) -> Math.abs  (args[0].asDouble()), 1, "abs"    );
		bindFunction(binder, (_,_,args) -> Math.floor(args[0].asDouble()), 1, "floor"  );
		bindFunction(binder, (_,_,args) -> Math.ceil (args[0].asDouble()), 1, "ceiling");
		
		bindFunction(binder, (_,_,args) -> Math.sin (args[0].asDouble()), 1, "sin" );
		bindFunction(binder, (_,_,args) -> Math.cos (args[0].asDouble()), 1, "cos" );
		bindFunction(binder, (_,_,args) -> Math.tan (args[0].asDouble()), 1, "tan" );
		
		bindFunction(binder, (_,_,args) -> Math.asin(args[0].asDouble()), 1, "asin");
		bindFunction(binder, (_,_,args) -> Math.acos(args[0].asDouble()), 1, "acos");
		bindFunction(binder, (_,_,args) -> Math.atan(args[0].asDouble()), 1, "atan");
		
		bindFunction(binder, (_,_,args) -> Math.sqrt(args[0].asDouble()), 1, "sqrt");
		
		bindFunction(binder, (_,_,args) -> Math.pow(args[0].asDouble(),args[1].asDouble()), 2, "pow");
		bindFunction(binder, (_,_,args) -> Math.min(args[0].asDouble(),args[1].asDouble()), 2, "min");
		bindFunction(binder, (_,_,args) -> Math.max(args[0].asDouble(),args[1].asDouble()), 2, "max");
		
		bindFunction(binder, (_,_,args) -> Math.floor(args[0].asDouble()*Math.pow(10, args[1].asInt()))
				/Math.pow(10, args[1].asInt()),2,"truncate");
		
		// Misc
		
		bindFunction(binder, (_,_,args)->{
			Object value = args[0].get();
			if (value instanceof Collection<?> c) return c.size();
			if (value instanceof Object[] c) return c.length;
			return args[0].asString().length();
		}, 1, "length");
		bindFunction(binder, (_,_,args)->(Iterable<Integer>)() -> {
			try {
				if (args.length==1) return new RangedIterator(0, args[0].asInt());
				return new RangedIterator(args[0].asInt(), args[1].asInt());
			} catch (ExecutionException e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
		}, 1, 2, "range");
	}
	
	public static void bindFunction(Binder binder, BindableFunction function, int length, String... names) {
		bindFunction(binder, function, length, length, names);
	}
	
	public static void bindFunction(Binder binder, BindableFunction function, int minlength, int maxlength, String... names) {
		for (String name : names) {
			BindableFunction expandedFunction = (uimanager, comp, args) -> {
				if (args.length<minlength) throw new ExecutionException("Too little parameters for "+name+" function!",-1,-1);
				if (args.length>maxlength) throw new ExecutionException("Too many parameters for "+name+" function!",-1,-1);
				return function.invoke(uimanager, comp, args);
			};
			binder.bindFunction(expandedFunction, name);
		}
	}

	public static String cleanDouble(double d) {
	    if(d == (long) d) return Long.toString((long)d);
	    else return Double.toString((long)d);
	}
}
