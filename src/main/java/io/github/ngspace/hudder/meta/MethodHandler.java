package io.github.ngspace.hudder.meta;

import static io.github.ngspace.hudder.meta.CompileState.BOTTOMLEFT;
import static io.github.ngspace.hudder.meta.CompileState.BOTTOMRIGHT;
import static io.github.ngspace.hudder.meta.CompileState.MUTE;
import static io.github.ngspace.hudder.meta.CompileState.TOPLEFT;
import static io.github.ngspace.hudder.meta.CompileState.TOPRIGHT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.methods.DecimalMethods;
import io.github.ngspace.hudder.meta.methods.GUIMethods;
import io.github.ngspace.hudder.meta.methods.IMethod;
import io.github.ngspace.hudder.meta.methods.InventoryInformationMethods;
import io.github.ngspace.hudder.meta.methods.ItemStackMethods;
import io.github.ngspace.hudder.meta.methods.LoadMethod;
import io.github.ngspace.hudder.meta.methods.StringMethods;
import io.github.ngspace.hudder.meta.methods.TextMethod;
import io.github.ngspace.hudder.meta.methods.TexturesMethods;
import io.github.ngspace.hudder.util.HudFileUtils;
import io.github.ngspace.hudder.util.MathUtils;
import net.minecraft.text.Text;

public class MethodHandler {
	public List<Consumer<MethodHandler>> customMethods = new ArrayList<Consumer<MethodHandler>>();
	public Map<String, IMethod> methods = new HashMap<String,IMethod>();
	public static final String[] Var = {"[Variable]"};
	public static final String[] TextArg = {"[Text]"};
	public MethodHandler() {
		
		registerRenderingMethods();
		
		
		
		//Logging and errors
		register((c,m,a,t,s)->Hudder.ins.player.sendMessage(Text.of(s[0].asString())),1, TextArg, "alert");
		register((c,m,a,t,s)->Hudder.log(s[0].asString()),1, TextArg, "log");
		register((c,m,a,t,s)->Hudder.warn(s[0].asString()),1, TextArg, "warn");
		register((c,m,a,t,s)->Hudder.error(s[0].asString()),1, TextArg, "error");
		register((c,m,a,t,s)->{throw new CompileException(s[0].asString());},1, TextArg, "throw");
		
		
		
		//Inventory Management
		register(new InventoryInformationMethods(), 2, new String[] {"[Slot number]",Var[0]}, "name", "durability",
				"maxdurability","count","maxcount");
		
		
		
		//Mathematical operations
		register(new DecimalMethods(), "decimalpoint", "float");
		/**This looks kinda confusing so I'll explain:
		 *   getAbsoluteValue() is the name the variable (aka the raw text supplied)
		 *   asInt() is the value of the variable as an int (aka the result of getVariable() converted to int)
		 */
		register((ci,meta,comp,type,args)->comp.put(args[0].toString(),args[0].asInt()),1,Var,"int","fullnumber");
		
		
		
		//String Manipulation
		register(new StringMethods(), "concat", "multiplystring", "substring", "string");
		
		

		//File-IO(THIS IS ALL YOU'RE GETTING, NO MORE FILE-IO! YOU'RE NOT WRITING OR READING FILES ON MY WATCH FUCKERS!)
		String[] ar = new String[] {"[Filename]",Var[0]};
		register((c,m,a,t,s)->a.put(s[1].getAbsoluteValue(), HudFileUtils.exists(s[0].asString())), 2, ar, "exists");
	}
	/**
	 * Calls the method with the name of the first value in the args parameter.
	 * @param config - The config used
	 * @param meta - The current meta
	 * @param compiler - The compiler
	 * @param args - the parameters
	 * @throws CompileException - if no message is found under the name found first value in the args parameter or
	 *  if the method itself called this exception.
	 */
	public void execute(ConfigInfo config, CompileState meta, ATextCompiler compiler, String[] args) throws CompileException {
		if (args.length==0) throw new CompileException("Empty method call");
		IMethod ameta = methods.get(args[0].toLowerCase());
		if (ameta==null) throw new CompileException("Unknown method " + args[0]);
		Value[] vals = new Value[args.length-1];
		for (int i = 0;i<vals.length;i++) vals[i] = new Value(args[i+1],compiler);
		ameta.invoke(config, meta, compiler, args[0], vals);
	}
	/**
	 * Register a meta method with multiple names
	 * @param method - the method
	 * @param names - the command
	 */
	public void register(IMethod method, String... names) {for(String name:names)methods.put(name,method);}
	/**
	 * Register a meta method with multiple names
	 * @param method - the method
	 * @param names - the command
	 */
	public void register(IMethod method, int length, String[] args, String... names) {
		IMethod newmethod = (config,meta,compiler,type,vals)->{
			if (vals.length<length) {
				String err='"'+type+"\" only accepts \""+type+"";
				for(String str:args)err+=", "+ str;
				err+='"';
				throw new CompileException(err);
			}
			method.invoke(config,meta,compiler,type,vals);
		};
		for (String name : names) methods.put(name,newmethod);
	}
	
	public static class Value {
		protected Value() {}
		private String arg;
		private ATextCompiler compiler;
		public Value(String value, ATextCompiler compiler) {
			this.arg=value;
			this.compiler=compiler;
		}
		public String getAbsoluteValue() {return arg;}
		@Override public String toString() {return getAbsoluteValue();}
		public ATextCompiler getCompiler() {return compiler;}
		
		public String asString() throws CompileException {return String.valueOf(compiler.getVariable(arg.trim()));}
		public int asInt() throws CompileException {return tryParseInt(compiler.getVariable(arg.trim()));}
		public double asDouble() throws CompileException {return tryParse(compiler.getVariable(arg.trim()));}
		public boolean asBoolean() {return tryParseBool(compiler.get(arg.trim()));}
	}
	
	public void registerRenderingMethods() {
		
		
		
		//Inventory Rendering
		register(new ItemStackMethods(),"slot","item","hand","selectedslot","hat", "helmet", "chestplate", "leggings",
				"pants", "boots", "offhand");
		
		
		
		//Text and compiling
		register((i,m,c,type,args)->m.setTextLocation(type,(float) (args.length>0?args[0].asDouble():i.scale)),
				BOTTOMRIGHT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, MUTE);
		register(new LoadMethod(), "load", "execute", "compile", "run", "add");
		register(new TextMethod(), "text");
		
		
		
		//UI
		register(new GUIMethods(), "health", "xpbar", "hotbar", "helditemtooltip");
		register(new TexturesMethods(), "image", "png", "texture");
		
		
		
		//Text Width
		register((c,m,a,t,s)->a.put(s[1].getAbsoluteValue(), Hudder.ins.textRenderer.getWidth(s[0].asString())),
				2, new String[] {"[Text]",Var[0]}, "strwidth");
	}
	/**
	 * Try to parse as double, if not return 0.
	 * @param obj - the object to parse
	 * @return a double representation of obj or 0.
	 */
	public static double tryParse(Object obj) {return MathUtils.tryParse(obj);}
	/**
	 * Try to parse as int, if not return 0.
	 * @param obj - the object to parse
	 * @return a int representation of obj or 0.
	 */
	public static int tryParseInt(Object obj) {return MathUtils.tryParseInt(obj);}
	/**
	 * Parse as boolean.
	 * @param obj - the object to parse
	 * @return a boolean representation of object.
	 */
	public static boolean tryParseBool(Object object) {
		if (object instanceof Boolean) return (boolean) object;
		return Boolean.valueOf(String.valueOf(object));
	}
}