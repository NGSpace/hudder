package dev.ngspace.hudder.v2runtime.methods;

import java.util.HashMap;
import java.util.Map;

import com.mojang.datafixers.types.templates.List;

import dev.ngspace.hudder.api.compilers.utils.CompileState;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;

public class MethodHandler {
	
	
	public static Map<String, V2IMethod> methods = new HashMap<String,V2IMethod>();
	public MethodHandler() {
		
		//Text and compiling
		bindConsumer((c,m,_,_,t,_,s)->m.setTextLocation(CompileState.Sections.valueOf(t.toUpperCase()),
				(float) (s.length>0?s[0].asDouble():c.scale())),
				CompileState.Sections.sectionNames());
		
		//Compiler and Variables
		bindConsumer(new LoadMethod(), "load", "execute", "compile", "run", "add");
		
		//Logging and errors
		bindConsumer((_,_,_,_,_,ch,s)->{throw new ExecutionException(s[0].asString(),ch);},1, new String[] {"[Text]"}, "throw");
	}
	
	
	public void bindConsumer(V2IMethod method, String... names) {
		for(String name:names)
			methods.put(name,method);
	}
	
	public void bindConsumer(V2IMethod method, int length, String[] args, String... names) {
		V2IMethod newmethod = (config,meta,compiler,runtime,name,charpos,vals) -> {
			if (vals.length<length) {
				String err='"'+name+"\" only accepts ;"+name+"";
				for(String str:args)err+=", "+ str;
				err+=';';
				throw new ExecutionException(err,charpos.line(),charpos.column());
			}
			method.invoke(config, meta, compiler, runtime, name, charpos, vals);
		};
		bindConsumer(newmethod, names);
	}
	
	
	/**
	 * Get the a registed method from it's name
	 * @param name - The name of the method.
	 * @return The method
	 * @throws CompileException - if there is no method with that name.
	 */
	public V2IMethod getMethodFromName(String name) throws IllegalArgumentException {
		V2IMethod method = methods.get(name.trim());
		if (method==null) throw new IllegalArgumentException("Unknown method " + name);
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
		V2IMethod newmethod = (_,state,comp,_,type,pos,vals) -> {
			if (vals.length!=argtypes.length) throw new ExecutionException(err, defline, defcharpos);
			for (int i = 0;i<vals.length;i++) {
				if      (parameters[i]==1) comp.putVariable("arg"+(i+1), vals[i].asString());
				else if (parameters[i]==2) comp.putVariable("arg"+(i+1), vals[i].asDouble());
				else if (parameters[i]==3) comp.putVariable("arg"+(i+1), vals[i].asBoolean());
				else if (parameters[i]==4) comp.putVariable("arg"+(i+1), vals[i].asType(List.class));
				else if (parameters[i]==0) comp.putVariable("arg"+(i+1), vals[i].get());
			}
			try {
				state.combineWithResult(comp.evalHud(method, filename).execute().toResult(), false);
			} catch (CompileException e) {
				throw new ExecutionException(e.getFailureMessage() +"\nMethod "+type+" threw an error ", pos);
			}
		};
		methods.put(name,newmethod);
	}
}