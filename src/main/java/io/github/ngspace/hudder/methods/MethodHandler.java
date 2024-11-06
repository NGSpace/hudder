package io.github.ngspace.hudder.methods;

import static io.github.ngspace.hudder.compilers.utils.CompileState.BOTTOMLEFT;
import static io.github.ngspace.hudder.compilers.utils.CompileState.BOTTOMRIGHT;
import static io.github.ngspace.hudder.compilers.utils.CompileState.MUTE;
import static io.github.ngspace.hudder.compilers.utils.CompileState.TOPLEFT;
import static io.github.ngspace.hudder.compilers.utils.CompileState.TOPRIGHT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.methods.elements.ColorVerticesElement;
import io.github.ngspace.hudder.methods.elements.TextureVerticesElement;
import io.github.ngspace.hudder.methods.methods.GUIMethods;
import io.github.ngspace.hudder.methods.methods.IMethod;
import io.github.ngspace.hudder.methods.methods.ItemStackMethods;
import io.github.ngspace.hudder.methods.methods.LoadMethod;
import io.github.ngspace.hudder.methods.methods.TextMethod;
import io.github.ngspace.hudder.methods.methods.TexturesMethods;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class MethodHandler {
	
	protected static MinecraftClient mc = MinecraftClient.getInstance();
	
	
	public List<Consumer<MethodHandler>> customMethods = new ArrayList<Consumer<MethodHandler>>();
	public Map<String, IMethod> methods = new HashMap<String,IMethod>();
	public static final String[] Var = {"[Variable]"};
	public static final String[] TextArg = {"[Text]"};
	public MethodHandler() {
		//Inventory Rendering
		bindConsumer(new ItemStackMethods(),"slot","item","hand","selectedslot","hat", "helmet", "chestplate", "leggings",
				"pants", "boots", "offhand");
		
		
		
		//Text and compiling
		bindConsumer((c,m,a,t,l,ch,s)->m.setTextLocation(t,(float) (s.length>0?s[0].asDouble():c.scale)),
				BOTTOMRIGHT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, MUTE);
		bindConsumer(new TextMethod(), "text");
		
		
		//UI
		bindConsumer(new GUIMethods(), "health", "statusbars", "xpbar", "hotbar", "helditemtooltip");
		bindConsumer(new TexturesMethods(), "image", "png", "texture");
		
		bindConsumer((c,m,a,t,l,ch,s)-> {
			try {
				m.elements.add(new TextureVerticesElement(s[0].asString(),s[1].asFloatArray(),s[2].asFloatArray()));
			} catch (IOException e) {
				throw new CompileException(e.getMessage(), l, ch, e);
			}
		}, "texturevertices");
		
		bindConsumer((c,m,a,t,l,ch,s)->m.elements.add(new ColorVerticesElement(s[0].asFloatArray(),s[1].asLong())),
				"colorvertices");
		
		
		
		//Compiler and Variables
		bindConsumer(new LoadMethod(), "load", "execute", "compile", "run", "add");
		bindConsumer((c,m,a,t,l,ch,s)->a.put(s[0].asString(), s[1]), "set", "setVal", "setVariable");
		
		
		
		//Logging and errors
		register((c,m,a,t,l,ch,s)->mc.player.sendMessage(Text.of(s[0].get().toString()),false),1, TextArg, "alert");
		register((c,m,a,t,l,ch,s)->Hudder.log(s[0].get().toString()),1, TextArg, "log");
		register((c,m,a,t,l,ch,s)->Hudder.warn(s[0].get().toString()),1, TextArg, "warn");
		register((c,m,a,t,l,ch,s)->Hudder.error(s[0].get().toString()),1, TextArg, "error");
		register((c,m,a,t,l,ch,s)->{throw new CompileException(s[0].asString(),l,ch);},1, TextArg, "throw");
	}
	
	
	public void bindConsumer(IMethod method, String... names) {for(String name:names)methods.put(name,method);}
	
	public void register(IMethod method, int length, String[] args, String... names) {
		IMethod newmethod = (config,meta,compiler,name,l,c,vals) -> {
			if (vals.length<length) {
				String err='"'+name+"\" only accepts ;"+name+"";
				for(String str:args)err+=", "+ str;
				err+=';';
				throw new CompileException(err,l,c);
			}
			method.invoke(config,meta,compiler,name,l,c,vals);
		};
		for (String name : names) methods.put(name,newmethod);
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
	
	
	public void register(String method, String[] argtypes, String name, int defline, int defcharpos, String filename) {
		int[] parameters = new int[argtypes.length];
		for (int i = 0;i<argtypes.length;i++) {
			if ("string".equals(argtypes[i])) parameters[i] = 1;
			else if ("number".equals(argtypes[i])) parameters[i] = 2;
			else if ("boolean".equals(argtypes[i])) parameters[i] = 3;
			else if ("array".equals(argtypes[i])) parameters[i] = 4;
			else if ("any".equals(argtypes[i])) parameters[i] = 5;
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
				else if (parameters[i]==4) comp.put("arg"+(i+1), vals[i].asList());
				else if (parameters[i]==5) comp.put("arg"+(i+1), vals[i].get());
			}
			try {
				state.combineWithResult(comp.compile(info, method, filename), false);
			} catch (CompileException e) {
				throw new CompileException(e.getFailureMessage() +"\nMethod "+type+" threw an error ", line, charpos);
			}
		};
		methods.put(name,newmethod);
	}
}