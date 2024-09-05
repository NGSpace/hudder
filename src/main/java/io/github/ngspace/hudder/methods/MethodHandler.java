package io.github.ngspace.hudder.methods;

import static io.github.ngspace.hudder.methods.CompileState.BOTTOMLEFT;
import static io.github.ngspace.hudder.methods.CompileState.BOTTOMRIGHT;
import static io.github.ngspace.hudder.methods.CompileState.MUTE;
import static io.github.ngspace.hudder.methods.CompileState.TOPLEFT;
import static io.github.ngspace.hudder.methods.CompileState.TOPRIGHT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.methods.methods.DecimalMethods;
import io.github.ngspace.hudder.methods.methods.GUIMethods;
import io.github.ngspace.hudder.methods.methods.IMethod;
import io.github.ngspace.hudder.methods.methods.InventoryInformationMethods;
import io.github.ngspace.hudder.methods.methods.ItemStackMethods;
import io.github.ngspace.hudder.methods.methods.LoadMethod;
import io.github.ngspace.hudder.methods.methods.StringMethods;
import io.github.ngspace.hudder.methods.methods.TextMethod;
import io.github.ngspace.hudder.methods.methods.TexturesMethods;
import io.github.ngspace.hudder.util.HudFileUtils;
import net.minecraft.text.Text;

public class MethodHandler {
	public List<Consumer<MethodHandler>> customMethods = new ArrayList<Consumer<MethodHandler>>();
	public Map<String, IMethod> methods = new HashMap<String,IMethod>();
	public static final String[] Var = {"[Variable]"};
	public static final String[] TextArg = {"[Text]"};
	public MethodHandler() {
		//Inventory Rendering
		register(new ItemStackMethods(),"slot","item","hand","selectedslot","hat", "helmet", "chestplate", "leggings",
				"pants", "boots", "offhand");
		
		
		
		//Text and compiling
		register((i,m,c,type,args)->m.setTextLocation(type,(float) (args.length>0?args[0].asDouble():i.scale)),
				BOTTOMRIGHT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, MUTE);
		register(new TextMethod(), "text");
		register((c,m,a,t,s)->a.put(s[1].getAbsoluteValue(), Hudder.ins.textRenderer.getWidth(s[0].asString())),
				2, new String[] {"[Text]",Var[0]}, "strwidth");
		
		
		
		//UI
		register(new GUIMethods(), "health", "xpbar", "hotbar", "helditemtooltip");
		register(new TexturesMethods(), "image", "png", "texture");
		
		
		
		//Compiler
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
		register((ci,meta,comp,type,args)->comp.put(args[0].getAbsoluteValue(),args[0].asInt()),1,Var,"int","fullnumber");
		
		
		
		//String Manipulation
		register(new StringMethods(), "concat", "multiplystring", "substring");
		
		

		//File-IO(THIS IS ALL YOU'RE GETTING, NO MORE FILE-IO! YOU'RE NOT WRITING OR READING FILES ON MY WATCH FUCKERS!)
		String[] ar = new String[] {"[Filename]",Var[0]};
		register((c,m,a,t,s)->a.put(s[1].getAbsoluteValue(), HudFileUtils.exists(s[0].asStringSafe())), 2, ar, "exists");
	}
	
	
	public void register(IMethod method, String... names) {for(String name:names)methods.put(name,method);}
	
	public void register(IMethod method, int length, String[] args, String... names) {
		IMethod newmethod = (config,meta,compiler,name,vals)->{
			
			if (vals.length<length) {
				String err='"'+name+"\" only accepts ;"+name+"";
				for(String str:args)err+=", "+ str;
				err+=';';
				throw new CompileException(err);
			}
			method.invoke(config,meta,compiler,name,vals);
		};
		for (String name : names) methods.put(name,newmethod);
	}
	
	public void register(String method, String[] argtypes, String name) {
		int[] parameters = new int[argtypes.length];
		for (int i = 0;i<argtypes.length;i++) {
			if ("string".equals(argtypes[i])) parameters[i] = 1;
			else if ("number".equals(argtypes[i])) parameters[i] = 2;
			else if ("boolean".equals(argtypes[i])) parameters[i] = 3;
			else if ("string_safe".equals(argtypes[i])) parameters[i] = 4;
			else if ("number_safe".equals(argtypes[i])) parameters[i] = 5;
			else if ("boolean_safe".equals(argtypes[i])) parameters[i] = 6;
		}
		String errb = '"'+name+"\" only accepts ;"+name+"";
		for (String arg : argtypes) errb += ", [" + arg + "]";
		errb+=';';
		String err = errb;
		IMethod newmethod = (info,state,comp,type,vals) -> {
			if (vals.length!=argtypes.length) throw new CompileException(err);
			for (int i = 0;i<vals.length;i++) {
				if      (parameters[i]==1) comp.put("arg"+(i+1), vals[i].asString());
				else if (parameters[i]==2) comp.put("arg"+(i+1), vals[i].asDouble());
				else if (parameters[i]==3) comp.put("arg"+(i+1), vals[i].asBoolean());
				else if (parameters[i]==4) comp.put("arg"+(i+1), vals[i].asStringSafe());
				else if (parameters[i]==5) comp.put("arg"+(i+1), vals[i].asDoubleSafe());
				else if (parameters[i]==6) comp.put("arg"+(i+1), vals[i].asBooleanSafe());
			}
			state.combineWithResult(comp.compile(info, method), false);
		};
		methods.put(name,newmethod);
	}
	
	
	/**
	 * Get the a registed method from it's name
	 * @param name - The name of the method.
	 * @return The method
	 * @throws CompileException - if there is no method with that name.
	 */
	public IMethod getMethodFromName(String name) throws CompileException {
		IMethod method = methods.get(name.toLowerCase());
		if (method==null) throw new CompileException("Unknown method " + name);
		return method;
	}
}