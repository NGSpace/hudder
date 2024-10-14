package io.github.ngspace.hudder.v2runtime.functions;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.util.HudFileUtils;

public class V2FunctionHandler {
	private Map<String, IV2Function> functions = new HashMap<String,IV2Function>();
	
	public V2FunctionHandler() {
		if (Hudder.IS_DEBUG) register(new TestFunction(), "test");
		
		//Type casting
		
		register(new DoubleV2Function(), 1, "int", "num", "number", "double"); // Int
		register(new StringV2Function(), "str", "string"); // Str
		
		//String manipulation

		register((r,n,args) -> args[0].asString() + args[1].asString(), 2, "concat");// Concat
		register((r,n,args) -> args[0].asString().substring(args[1].asInt(),args[2].asInt()),3,"substring");// Substring
		register((r,n,args) -> args[0].asString().repeat(args[1].asInt()),3, "multiplystring", "repeat");// Repeat string
		register((r,n,args) -> args[0].asString().length(), 1, "length");// Length
		
		//Math
		
		register(new RngV2Function(), "rng", "random"); // Rng

		register((r,n,args) -> Math.abs  (args[0].asDouble()) ,1, "abs"    );// Abs
		register((r,n,args) -> Math.floor(args[0].asDouble()) ,1, "floor"  );// Floor
		register((r,n,args) -> Math.ceil (args[0].asDouble()) ,1, "ceiling");// Ceiling
		
		register((r,n,args) -> Math.sin(args[0].asDouble()) ,1, "sin" );// Sin
		register((r,n,args) -> Math.cos(args[0].asDouble()) ,1, "cos" );// Cos
		register((r,n,args) -> Math.tan(args[0].asDouble()) ,1, "tan" );// Tan
		
		register((r,n,args) -> Math.asin(args[0].asDouble()),1, "asin");// Asin
		register((r,n,args) -> Math.acos(args[0].asDouble()),1, "acos");// Acos
		register((r,n,args) -> Math.atan(args[0].asDouble()),1, "atan");// Atan
		
		register((r,n,args) -> Math.sqrt(args[0].asDouble()),1, "sqrt");// Sqrt
		
		register((r,n,args) -> Math.pow(args[0].asDouble(),args[1].asDouble()), 2, "pow");// Pow
		register((r,n,args) -> Math.min(args[0].asDouble(),args[1].asDouble()), 2, "min");// Min
		register((r,n,args) -> Math.max(args[0].asDouble(),args[1].asDouble()), 2, "max");// Max
		
		register((r,n,args) -> Math.floor(args[1].asDouble()*args[1].asInt())/args[1].asInt(),2,"truncate");// Truncate
		
		// Misc
		
		register((r,n,args) -> HudFileUtils.exists(args[0].asString()), 1, "exists");// Exists
		register((r,n,args) -> Hudder.ins.textRenderer.getWidth(args[0].asString()), 1, "strwidth");// Strwidth
		
	}

	public void register(IV2Function function, String... names) {
		for(String name:names) functions.put(name,function);
	}

	public void register(IV2Function function, int length, String... names) {
		IV2Function expandedFunction = (runtime, name, args) -> {
			if (args.length<length) throw new CompileException("Too little parameters for "+name+"!");
			if (args.length>length) throw new CompileException("Too many parameters for "+name+"!");
			return function.execute(runtime, name, args);
		};
		for(String name:names) functions.put(name,expandedFunction);
	}
	
	public IV2Function getFunction(String name) {
		return functions.get(name);
	}
}
