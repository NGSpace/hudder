package dev.ngspace.hudder.v2runtime.functions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import dev.ngspace.hudder.v2runtime.values.AV2Value;
import net.minecraft.client.Minecraft;

public class V2FunctionHandler {

	private static Map<String, IV2Function> functions = new HashMap<String,IV2Function>();
	
	protected static Minecraft mc = Minecraft.getInstance();
	
	public V2FunctionHandler() {
		bindAllAPIFunctions();
	}

	public void bindAllAPIFunctions() {
		
		//Type casting
		
		bindFunction(new DoubleV2Function(), 1, "int", "num", "number", "double");
		bindFunction(new StringV2Function(), 1, 2, "str", "string");
		bindFunction(new ArrayV2Function(), 1, 2, "array");
		bindFunction((_,_,args,_,_) -> (char)(args[0].asDouble()), 1, "char");
		bindFunction((_,_,args,_,_) -> Integer.toBinaryString((int) args[0].asDouble()), 1, "toBinaryString");
		
		
		//String manipulation

		bindFunctionDep((_,_,args,_,_)->args[0].asString()+args[1].asString(),2,2,"Use \"[string].concat([string])\" function", "concat");
		bindFunctionDep((_,_,args,_,_)->args[0].asString().substring(args[1].asInt(),args[2].asInt()),3,3,"Use \"[string].substring([number], [number])\" function","substring");
		bindFunctionDep((_,_,args,_,_)->args[0].asString().repeat(args[1].asInt()),2,2,"Use \"[string].repeat([number])\" function", "multiplystring", "repeat");
		
		//Math
		
		bindFunction(new RngV2Function(), 2, 3, "rng", "random");

		bindFunction((_,_,args,_,_) -> Math.abs  (args[0].asDouble()), 1, "abs"    );
		bindFunction((_,_,args,_,_) -> Math.floor(args[0].asDouble()), 1, "floor"  );
		bindFunction((_,_,args,_,_) -> Math.ceil (args[0].asDouble()), 1, "ceiling");
		
		bindFunction((_,_,args,_,_) -> Math.sin (args[0].asDouble()), 1, "sin" );
		bindFunction((_,_,args,_,_) -> Math.cos (args[0].asDouble()), 1, "cos" );
		bindFunction((_,_,args,_,_) -> Math.tan (args[0].asDouble()), 1, "tan" );
		
		bindFunction((_,_,args,_,_) -> Math.asin(args[0].asDouble()), 1, "asin");
		bindFunction((_,_,args,_,_) -> Math.acos(args[0].asDouble()), 1, "acos");
		bindFunction((_,_,args,_,_) -> Math.atan(args[0].asDouble()), 1, "atan");
		
		bindFunction((_,_,args,_,_) -> Math.sqrt(args[0].asDouble()), 1, "sqrt");
		
		bindFunction((_,_,args,_,_) -> Math.pow(args[0].asDouble(),args[1].asDouble()), 2, "pow");
		bindFunction((_,_,args,_,_) -> Math.min(args[0].asDouble(),args[1].asDouble()), 2, "min");
		bindFunction((_,_,args,_,_) -> Math.max(args[0].asDouble(),args[1].asDouble()), 2, "max");
		
		bindFunction((_,_,args,_,_) -> Math.floor(args[0].asDouble()*Math.pow(10, args[1].asInt()))
				/Math.pow(10, args[1].asInt()),2,"truncate");
		
		//Inventory management

		bindFunctionDep((_,_,args,_,_)->mc.player.getInventory().getItem(args[0].asInt()).getDisplayName(),1,1,
				"Use \"getItem([number]).name\"","itemname");
		bindFunctionDep((_,_,args,_,_)->mc.player.getInventory().getItem(args[0].asInt()).getCount(),1,1,"itemcount");
		
		
		bindFunctionDep((_,_,args,_,_)->{
			var stack = mc.player.getInventory().getItem(args[0].asInt());
			return stack.getMaxDamage()-stack.getDamageValue();
		},1,1,"itemdurability");
		
		
		bindFunctionDep((_,_,args,_,_)->mc.player.getInventory().getItem(args[0].asInt()).getMaxDamage(),1,1,
				"itemmaxdurability");
		bindFunctionDep((_,_,args,_,_)->mc.player.getInventory().getItem(args[0].asInt()).getMaxStackSize(),1,1,
				"itemmaxcount");
		
		// Misc
		
		bindFunction(new LengthV2Function(), 1, "length");
		bindFunction((_,_,args,_,_)->(Iterable<Integer>)() -> {
			try {
				if (args.length==1) return new RangedIterator(0, args[0].asInt());
				return new RangedIterator(args[0].asInt(), args[1].asInt());
			} catch (ExecutionException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
		}, 1, 2, "range");
	}
	
	
	
	private void bindFunctionDep(IV2Function func, int minlength, int maxlength, String message, String... names) {
		IV2Function expandedFunction = new IV2Function() {
			@Override
			public Object execute(V2Runtime runtime, String name, AV2Value[] args, int line, int charpos)
					throws ExecutionException {
				if (args.length<minlength) throw new ExecutionException("Too little parameters for "+name+" function!",line,charpos);
				if (args.length>maxlength) throw new ExecutionException("Too many parameters for "+name+" function!",line,charpos);
				return func.execute(runtime, name, args, line, charpos);
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
			if (args.length<minlength) throw new ExecutionException("Too little parameters for "+name+" function!",line,charpos);
			if (args.length>maxlength) throw new ExecutionException("Too many parameters for "+name+" function!",line,charpos);
			return function.execute(runtime, name, args, line, charpos);
		};
		bindFunction(expandedFunction,names);
	}
	
	
	
	
	public IV2Function getFunction(String name) {
		return functions.get(name);
	}
	
	
	
	public class RangedIterator implements Iterator<Integer> {
		
		private int index;
		private int end;

		public RangedIterator(int start, int end) {
			if (start>end)
				throw new IllegalArgumentException("Start (" + start + ") can not be greater than end (" + end + ")!");
			this.index = start;
			this.end = end;
		}

		@Override
		public boolean hasNext() {
			return index<end;
		}
		
		@Override
		public Integer next() {
			if (index>end) throw new NoSuchElementException("Went past end of iterable!");
			return index++;
		}
		
	}
}
