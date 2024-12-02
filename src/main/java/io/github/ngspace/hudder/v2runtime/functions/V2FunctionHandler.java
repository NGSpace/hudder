package io.github.ngspace.hudder.v2runtime.functions;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.v2runtime.V2Runtime;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;
import net.minecraft.client.MinecraftClient;

public class V2FunctionHandler {
	private Map<String, IV2Function> functions = new HashMap<String,IV2Function>();
	
	protected static MinecraftClient mc = MinecraftClient.getInstance();
	
	public V2FunctionHandler() {
		if (Hudder.IS_DEBUG) bindFunction(new TestFunction(), 2, "test");
		
		//Type casting
		
		bindFunction(new DoubleV2Function(), 1, "int", "num", "number", "double");
		bindFunction(new StringV2Function(), 1, 2, "str", "string");
		bindFunction(new ArrayV2Function(), 1, "array");
		
		//String manipulation

		bindFunctionDep((r,n,args,l,c)->args[0].asString()+args[1].asString(),2,2,"Use \"[string].concat([string])\" function", "concat");
		bindFunctionDep((r,n,args,l,c)->args[0].asString().substring(args[1].asInt(),args[2].asInt()),3,3,"Use \"[string].substring([number], [number])\" function","substring");
		bindFunctionDep((r,n,args,l,c)->args[0].asString().repeat(args[1].asInt()),2,2,"Use \"[string].repeat([number])\" function", "multiplystring", "repeat");
		
		//Math
		
		bindFunction(new RngV2Function(), 2, 3, "rng", "random");

		bindFunction((r,n,args,l,c) -> Math.abs  (args[0].asDouble()), 1, "abs"    );
		bindFunction((r,n,args,l,c) -> Math.floor(args[0].asDouble()), 1, "floor"  );
		bindFunction((r,n,args,l,c) -> Math.ceil (args[0].asDouble()), 1, "ceiling");
		
		bindFunction((r,n,args,l,c) -> Math.sin (args[0].asDouble()), 1, "sin" );
		bindFunction((r,n,args,l,c) -> Math.cos (args[0].asDouble()), 1, "cos" );
		bindFunction((r,n,args,l,c) -> Math.tan (args[0].asDouble()), 1, "tan" );
		
		bindFunction((r,n,args,l,c) -> Math.asin(args[0].asDouble()), 1, "asin");
		bindFunction((r,n,args,l,c) -> Math.acos(args[0].asDouble()), 1, "acos");
		bindFunction((r,n,args,l,c) -> Math.atan(args[0].asDouble()), 1, "atan");
		
		bindFunction((r,n,args,l,c) -> Math.sqrt(args[0].asDouble()), 1, "sqrt");
		
		bindFunction((r,n,args,l,c) -> Math.pow(args[0].asDouble(),args[1].asDouble()), 2, "pow");
		bindFunction((r,n,args,l,c) -> Math.min(args[0].asDouble(),args[1].asDouble()), 2, "min");
		bindFunction((r,n,args,l,c) -> Math.max(args[0].asDouble(),args[1].asDouble()), 2, "max");
		
		bindFunction((r,n,args,l,c) -> Math.floor(args[0].asDouble()*Math.pow(10, args[1].asInt()))
				/Math.pow(10, args[1].asInt()),2,"truncate");
		
		//Inventory management

		bindFunctionDep((r,n,args,l,c)->mc.player.getInventory().getStack(args[0].asInt()).getName(),1,1,
				"Use \"getItem([number]).name\"","itemname");
		bindFunctionDep((r,n,args,l,c)->mc.player.getInventory().getStack(args[0].asInt()).getCount(),1,1,"itemcount");
		
		
		bindFunctionDep((r,n,args,l,c)->{
			var stack = mc.player.getInventory().getStack(args[0].asInt());
			return stack.getMaxDamage()-stack.getDamage();
		},1,1,"itemdurability");
		
		
		bindFunctionDep((r,n,args,l,c)->mc.player.getInventory().getStack(args[0].asInt()).getMaxDamage(),1,1,
				"itemmaxdurability");
		bindFunctionDep((r,n,args,l,c)->mc.player.getInventory().getStack(args[0].asInt()).getMaxCount(),1,1,
				"itemmaxcount");
		
		// Misc
		
		bindFunction(new LengthV2Function(), 1, "length");
		bindFunction((r,n,args,l,c)->new HashMap<Object, Object>(),0, "map");
		
	}

	private void bindFunctionDep(IV2Function func, int minlength, int maxlength, String message, String... names) {
		IV2Function expandedFunction = new IV2Function() {
			@Override
			public Object execute(V2Runtime runtime, String name, AV2Value[] args, int line, int charpos)
					throws CompileException {
				if (args.length<minlength) throw new CompileException("Too little parameters for "+name+" function!",line,charpos);
				if (args.length>maxlength) throw new CompileException("Too many parameters for "+name+" function!",line,charpos);
				return func.execute(runtime, name, args,line,charpos);
			}
			@Override public String getDeprecationWarning(String funcname) {return message;}
			@Override public boolean isDeprecated(String funcname) {return true;}
		};
		bindFunction(expandedFunction, names);
	}

	public void bindFunction(IV2Function function, String... names) {
		for(String name:names) functions.put(name,function);
	}

	public void bindFunction(IV2Function function, int length, String... names) {
		bindFunction(function, length, length, names);
	}
	public void bindFunction(IV2Function function, int minlength, int maxlength, String... names) {
		IV2Function expandedFunction = (runtime, name, args, line, charpos) -> {
			if (args.length<minlength) throw new CompileException("Too little parameters for "+name+" function!",line,charpos);
			if (args.length>maxlength) throw new CompileException("Too many parameters for "+name+" function!",line,charpos);
			return function.execute(runtime, name, args,line,charpos);
		};
		bindFunction(expandedFunction,names);
	}
	
	public IV2Function getFunction(String name) {
		return functions.get(name);
	}
}