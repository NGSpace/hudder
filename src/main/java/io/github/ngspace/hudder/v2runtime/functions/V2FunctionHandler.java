package io.github.ngspace.hudder.v2runtime.functions;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.util.HudFileUtils;
import net.minecraft.client.MinecraftClient;

public class V2FunctionHandler {
	private Map<String, IV2Function> functions = new HashMap<String,IV2Function>();
	
	protected static MinecraftClient mc = MinecraftClient.getInstance();
	
	public V2FunctionHandler() {
		if (Hudder.IS_DEBUG) register(new TestFunction(), 2, "test");
		
		//Type casting
		
		register(new DoubleV2Function(), 1, "int", "num", "number", "double");
		register(new StringV2Function(), 1, 2, "str", "string");
		register(new ArrayV2Function(), 1, "array");
		
		//String manipulation

		register((r,n,args,l,c) -> args[0].asString() + args[1].asString(), 2, "concat");
		register((r,n,args,l,c) -> args[0].asString().substring(args[1].asInt(),args[2].asInt()),3,"substring");
		register((r,n,args,l,c) -> args[0].asString().repeat(args[1].asInt()),2, "multiplystring", "repeat");
		
		//Math
		
		register(new RngV2Function(), 2, 3, "rng", "random"); // Rng

		register((r,n,args,l,c) -> Math.abs  (args[0].asDouble()) ,1, "abs"    );
		register((r,n,args,l,c) -> Math.floor(args[0].asDouble()) ,1, "floor"  );
		register((r,n,args,l,c) -> Math.ceil (args[0].asDouble()) ,1, "ceiling");
		
		register((r,n,args,l,c) -> Math.sin(args[0].asDouble()) ,1, "sin" );
		register((r,n,args,l,c) -> Math.cos(args[0].asDouble()) ,1, "cos" );
		register((r,n,args,l,c) -> Math.tan(args[0].asDouble()) ,1, "tan" );
		
		register((r,n,args,l,c) -> Math.asin(args[0].asDouble()),1, "asin");
		register((r,n,args,l,c) -> Math.acos(args[0].asDouble()),1, "acos");
		register((r,n,args,l,c) -> Math.atan(args[0].asDouble()),1, "atan");
		
		register((r,n,args,l,c) -> Math.sqrt(args[0].asDouble()),1, "sqrt");
		
		register((r,n,args,l,c) -> Math.pow(args[0].asDouble(),args[1].asDouble()), 2, "pow");
		register((r,n,args,l,c) -> Math.min(args[0].asDouble(),args[1].asDouble()), 2, "min");
		register((r,n,args,l,c) -> Math.max(args[0].asDouble(),args[1].asDouble()), 2, "max");
		
		register((r,n,args,l,c) -> Math.floor(args[0].asDouble()*Math.pow(10, args[1].asInt()))
				/Math.pow(10, args[1].asInt()),2,"truncate");
		
		//Inventory management

		register((r,n,args,l,c)->mc.player.getInventory().getStack(args[0].asInt()).getName(),1,"itemname");
		register((r,n,args,l,c)->mc.player.getInventory().getStack(args[0].asInt()).getCount(),1,"itemcount");
		
		
		register((r,n,args,l,c)->{
			var stack = mc.player.getInventory().getStack(args[0].asInt());
			return stack.getMaxDamage()-stack.getDamage();
		},1,"itemdurability");
		
		
		register((r,n,args,l,c)->mc.player.getInventory().getStack(args[0].asInt()).getMaxDamage(),1,
				"itemmaxdurability");
		register((r,n,args,l,c)->mc.player.getInventory().getStack(args[0].asInt()).getMaxCount(),1,
				"itemmaxcount");
		
		// Misc
		
		register((r,n,args,l,c) -> HudFileUtils.exists(args[0].asString()), 1, "exists");
		register((r,n,args,l,c) -> mc.textRenderer.getWidth(args[0].asString()), 1, "strwidth");
		register(new LengthV2Function(), 1, "length");// Length
		
	}

	public void register(IV2Function function, String... names) {
		for(String name:names) functions.put(name,function);
	}

	public void register(IV2Function function, int length, String... names) {
		register(function,length,length, names);
	}
	public void register(IV2Function function, int minlength, int maxlength, String... names) {
		IV2Function expandedFunction = (runtime, name, args, line, charpos) -> {
			if (args.length<minlength) throw new CompileException("Too little parameters for "+name+"!",line,charpos);
			if (args.length>maxlength) throw new CompileException("Too many parameters for "+name+"!",line,charpos);
			return function.execute(runtime, name, args,line,charpos);
		};
		register(expandedFunction,names);
	}
	
	public IV2Function getFunction(String name) {
		return functions.get(name);
	}
}
