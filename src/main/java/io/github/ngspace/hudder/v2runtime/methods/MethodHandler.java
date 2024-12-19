package io.github.ngspace.hudder.v2runtime.methods;

import static io.github.ngspace.hudder.compilers.utils.CompileState.BOTTOMLEFT;
import static io.github.ngspace.hudder.compilers.utils.CompileState.BOTTOMRIGHT;
import static io.github.ngspace.hudder.compilers.utils.CompileState.MUTE;
import static io.github.ngspace.hudder.compilers.utils.CompileState.TOPLEFT;
import static io.github.ngspace.hudder.compilers.utils.CompileState.TOPRIGHT;

import java.util.HashMap;
import java.util.Map;

import com.mojang.datafixers.types.templates.List;

import io.github.ngspace.hudder.compilers.utils.CompileException;

public class MethodHandler {
	
	
	public static Map<String, IMethod> methods = new HashMap<String,IMethod>();
	public static final String[] Var = {"[Variable]"};
	public static final String[] TextArg = {"[Text]"};
	public MethodHandler() {
		//Inventory Rendering
		bindConsumer(new ItemStackMethods(),"slot","item","hand","selectedslot","hat", "helmet", "chestplate", "leggings",
				"pants", "boots", "offhand");
		
		//Text and compiling
		bindConsumer((c,m,a,t,l,ch,s)->m.setTextLocation(t,(float) (s.length>0?s[0].asDouble():c.scale)),
				BOTTOMRIGHT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, MUTE);
		
		//Compiler and Variables
		bindConsumer(new LoadMethod(), "load", "execute", "compile", "run", "add");
		
		//Logging and errors
		bindConsumer((c,m,a,t,l,ch,s)->{throw new CompileException(s[0].asString(),l,ch);},1, TextArg, "throw");
	}
	
	
	public void bindConsumer(IMethod method, String... names) {for(String name:names)methods.put(name,method);}
	
	public void bindConsumer(IMethod method, int length, String[] args, String... names) {
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
		IMethod method = methods.get(name.toLowerCase().trim());
		if (method==null) throw new CompileException("Unknown method " + name);
		return method;
	}
	
	/**
	 * @deprecated Make yo own damn method bitch.
	 * <br><br>
	 * Just extend IMethod and compile.
	 */
	@Deprecated(since = "7.2.0", forRemoval = true)
	public void register(String method, String[] argtypes, String name, int defline, int defcharpos, String filename) {
		int[] parameters = new int[argtypes.length];
		for (int i = 0;i<argtypes.length;i++) {
			if ("string".equals(argtypes[i].trim())) parameters[i] = 1;
			else if ("number".equals(argtypes[i].trim())) parameters[i] = 2;
			else if ("boolean".equals(argtypes[i].trim())) parameters[i] = 3;
			else if ("array".equals(argtypes[i].trim())) parameters[i] = 4;
			else if ("any".equals(argtypes[i].trim())) parameters[i] = 0;
			else throw new UnsupportedOperationException("Can't recognize type: " + argtypes[i].trim());
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
				else if (parameters[i]==4) comp.put("arg"+(i+1), vals[i].asType(List.class));
				else if (parameters[i]==0) comp.put("arg"+(i+1), vals[i].get());
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