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
import io.github.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
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
import net.minecraft.text.Text;

public class MethodHandler {
	public List<Consumer<MethodHandler>> customMethods = new ArrayList<Consumer<MethodHandler>>();
	public Map<String, IMethod> methods = new HashMap<String,IMethod>();
	public static final String[] Var = {"[Variable]"};
	public static final String[] TextArg = {"[Text]"};
	public MethodHandler() {
		
		registerRenderingMethods();
		
		
		register(new LoadMethod(), "load", "execute", "compile", "run", "add");
		
		
		
		//Logging and errors
		register((c,m,a,t,s)->Hudder.ins.player.sendMessage(Text.of(s[0].asStringSafe())),1, TextArg, "alert");
		register((c,m,a,t,s)->Hudder.log(s[0].asStringSafe()),1, TextArg, "log");
		register((c,m,a,t,s)->Hudder.warn(s[0].asStringSafe()),1, TextArg, "warn");
		register((c,m,a,t,s)->Hudder.error(s[0].asStringSafe()),1, TextArg, "error");
		register((c,m,a,t,s)->{throw new CompileException(s[0].asStringSafe());},1, TextArg, "throw");
		
		
		
		//Inventory Management
		register(new InventoryInformationMethods(), 2, new String[] {"[Slot number]",Var[0]}, "name", "durability",
				"maxdurability","count","maxcount");
		
		
		
		//Mathematical operations
		register(new DecimalMethods(), "decimalpoint", "float");
		/**This looks kinda confusing so I'll explain:
		 *   getAbsoluteValue() is the name the variable (aka the raw text supplied)
		 *   asInt() is the value of the variable as an int (aka the result of getVariable() converted to int)
		 */
		register((ci,meta,comp,type,args)->comp.put(args[0].asStringSafe(),args[0].asInt()),1,Var,"int","fullnumber");
		
		
		
		//String Manipulation
		register(new StringMethods(), "concat", "multiplystring", "substring");
		
		

		//File-IO(THIS IS ALL YOU'RE GETTING, NO MORE FILE-IO! YOU'RE NOT WRITING OR READING FILES ON MY WATCH FUCKERS!)
		String[] ar = new String[] {"[Filename]",Var[0]};
		register((c,m,a,t,s)->a.put(s[1].getAbsoluteValue(), HudFileUtils.exists(s[0].asStringSafe())), 2, ar, "exists");
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
	public void execute(ConfigInfo config, CompileState meta, AVarTextCompiler compiler, String[] args) throws CompileException {
		if (args.length==0) throw new CompileException("Empty method call");
		IMethod ameta = methods.get(args[0].toLowerCase());
		if (ameta==null) throw new CompileException("Unknown method " + args[0]);
		MethodValue[] vals = new MethodValue[args.length-1];
		for (int i = 0;i<vals.length;i++) vals[i] = new MethodValue(args[i+1],compiler);
		ameta.invoke(config, meta, compiler, args[0], vals);
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
	public void execute(ConfigInfo config, CompileState meta, AVarTextCompiler compiler, String type, MethodValue[] vals) throws CompileException {
		IMethod ameta = methods.get(type.toLowerCase());
		if (ameta==null) throw new CompileException("Unknown method " + type);
		ameta.invoke(config, meta, compiler, type, vals);
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
				String err='"'+type+"\" only accepts ;"+type+"";
				for(String str:args)err+=", "+ str;
				err+=';';
				throw new CompileException(err);
			}
			method.invoke(config,meta,compiler,type,vals);
		};
		for (String name : names) methods.put(name,newmethod);
	}
	
	
	
	public IMethod getMethod(String name) throws CompileException {
		IMethod method = methods.get(name.toLowerCase());
		if (method==null) throw new CompileException("Unknown method " + name);
		return method;
	}
	
	
	
	public void registerRenderingMethods() {
		
		//Inventory Rendering
		register(new ItemStackMethods(),"slot","item","hand","selectedslot","hat", "helmet", "chestplate", "leggings",
				"pants", "boots", "offhand");
		
		
		
		//Text and compiling
		register((i,m,c,type,args)->m.setTextLocation(type,(float) (args.length>0?args[0].asDouble():i.scale)),
				BOTTOMRIGHT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, MUTE);
		register(new TextMethod(), "text");
		register((c,m,a,t,s)->a.put(s[1].asStringSafe(), Hudder.ins.textRenderer.getWidth(s[0].asString())),
				2, new String[] {"[Text]",Var[0]}, "strwidth");
		
		
		
		//UI
		register(new GUIMethods(), "health", "xpbar", "hotbar", "helditemtooltip");
		register(new TexturesMethods(), "image", "png", "texture");
	}
}