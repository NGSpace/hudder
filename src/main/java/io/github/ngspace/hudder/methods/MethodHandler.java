package io.github.ngspace.hudder.methods;

import static io.github.ngspace.hudder.compilers.utils.CompileState.BOTTOMLEFT;
import static io.github.ngspace.hudder.compilers.utils.CompileState.BOTTOMRIGHT;
import static io.github.ngspace.hudder.compilers.utils.CompileState.MUTE;
import static io.github.ngspace.hudder.compilers.utils.CompileState.TOPLEFT;
import static io.github.ngspace.hudder.compilers.utils.CompileState.TOPRIGHT;

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
		register((c,m,a,t,l,ch,s)->m.setTextLocation(t,(float) (s.length>0?s[0].asDouble():c.scale)),
				BOTTOMRIGHT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, MUTE);
		register(new TextMethod(), "text");
		
		
		//UI
		register(new GUIMethods(), "health", "xpbar", "hotbar", "helditemtooltip");
		register(new TexturesMethods(), "image", "png", "texture");
		
		
		
		//Compiler
		register(new LoadMethod(), "load", "execute", "compile", "run", "add");
		
		
		
		//Logging and errors
		register((c,m,a,t,l,ch,s)->Hudder.ins.player.sendMessage(Text.of(s[0].asString())),1, TextArg, "alert");
		register((c,m,a,t,l,ch,s)->Hudder.log(s[0].asString()),1, TextArg, "log");
		register((c,m,a,t,l,ch,s)->Hudder.warn(s[0].asString()),1, TextArg, "warn");
		register((c,m,a,t,l,ch,s)->Hudder.error(s[0].asString()),1, TextArg, "error");
		register((c,m,a,t,l,ch,s)->{throw new CompileException(s[0].asString());},1, TextArg, "throw");
		
		
		
		//Inventory Management
		register(new InventoryInformationMethods(), 2, new String[] {"[Slot number]",Var[0]}, "name", "durability",
				"maxdurability","count","maxcount");
		
		
		
		//Mathematical operations
		register(new DecimalMethods(), "decimalpoint", "float");
		
		
		
		//String Manipulation
		register(new StringMethods(), "concat", "multiplystring", "substring");
	}
	
	
	public void register(IMethod method, String... names) {for(String name:names)methods.put(name,method);}
	
	public void register(IMethod method, int length, String[] args, String... names) {
		IMethod newmethod = (config,meta,compiler,name,l,c,vals)->{
			
			if (vals.length<length) {
				String err='"'+name+"\" only accepts ;"+name+"";
				for(String str:args)err+=", "+ str;
				err+=';';
				throw new CompileException(err);
			}
			method.invoke(config,meta,compiler,name,l,c,vals);
		};
		for (String name : names) methods.put(name,newmethod);
	}
	
	@SuppressWarnings("removal")
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
		IMethod newmethod = (info,state,comp,type,line,charpos,vals) -> {
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


	@SuppressWarnings("removal")
	public void register(String method, String[] argtypes, String name, int defline, int defcharpos) {
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
		IMethod newmethod = (info,state,comp,type,line,charpos,vals) -> {
			if (vals.length!=argtypes.length) throw new CompileException(err, defline, defcharpos);
			for (int i = 0;i<vals.length;i++) {
				if      (parameters[i]==1) comp.put("arg"+(i+1), vals[i].asString());
				else if (parameters[i]==2) comp.put("arg"+(i+1), vals[i].asDouble());
				else if (parameters[i]==3) comp.put("arg"+(i+1), vals[i].asBoolean());
				else if (parameters[i]==4) comp.put("arg"+(i+1), vals[i].asStringSafe());
				else if (parameters[i]==5) comp.put("arg"+(i+1), vals[i].asDoubleSafe());
				else if (parameters[i]==6) comp.put("arg"+(i+1), vals[i].asBooleanSafe());
			}
			try {
				state.combineWithResult(comp.compile(info, method), false);
			} catch (CompileException e) {
				throw new CompileException(e.getFailureMessage() +"\nMethod threw an error " + type, line, charpos);
			}
		};
		methods.put(name,newmethod);
	}
}