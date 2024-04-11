package io.github.ngspace.hudder.meta;

import static io.github.ngspace.hudder.meta.Meta.BOTTOMLEFT;
import static io.github.ngspace.hudder.meta.Meta.BOTTOMRIGHT;
import static io.github.ngspace.hudder.meta.Meta.MUTE;
import static io.github.ngspace.hudder.meta.Meta.TOPLEFT;
import static io.github.ngspace.hudder.meta.Meta.TOPRIGHT;
import static io.github.ngspace.hudder.util.MathUtils.tryParse;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.methods.DecimalMethod;
import io.github.ngspace.hudder.meta.methods.IMethod;
import io.github.ngspace.hudder.meta.methods.ItemStackMethods;
import io.github.ngspace.hudder.meta.methods.LoadMethod;
import io.github.ngspace.hudder.meta.methods.StringMethods;
import io.github.ngspace.hudder.meta.methods.TextMethod;

public class MetaCompiler {
	public static Map<String, IMethod> methods = new HashMap<String,IMethod>();
	static {
		IMethod itemstackmethod = new ItemStackMethods();
		register("slot", itemstackmethod);
		register("item", itemstackmethod);
		register("hand", itemstackmethod);
		register("selectedslot", itemstackmethod);
		register("hat", itemstackmethod);
		register("helmet", itemstackmethod);
		register("chestplate", itemstackmethod);
		register("leggings", itemstackmethod);
		register("pants", itemstackmethod);
		register("boots", itemstackmethod);
		register("offhand", itemstackmethod);
		IMethod v=(ci,meta,c,args)->meta.setTextLocation(args[0],(float) (args.length>1?tryParse(args[1]):ci.scale));
		register(BOTTOMRIGHT, v);
		register(TOPLEFT, v);
		register(TOPRIGHT, v);
		register(BOTTOMLEFT, v);
		register(MUTE, v);
		IMethod loadMethod = new LoadMethod();
		register("load", loadMethod);
		register("execute", loadMethod);
		register("compile", loadMethod);
		register("run", loadMethod);
		register("add", loadMethod);
		IMethod textMethods = new TextMethod();
		register("text", textMethods);
		IMethod intify = (ci,meta,comp,args) -> {
			try {
				comp.put(args[1],(int)tryParse(comp.getVariable(args[1].trim())));
			} catch (IndexOutOfBoundsException e) {
				throw new CompileException("\""+args[0]+"\" only accepts \""+args[0]+",[Variable]\"");
			}
		};
		register("int",intify);
		register("fullnumber",intify);
		IMethod decimalMethods = new DecimalMethod();
		register("decimalpoint", decimalMethods);
		register("float", decimalMethods);
		IMethod stringMethods = new StringMethods();
		register("concat", stringMethods);
		register("multiplystring", stringMethods);
		register("substring", stringMethods);
		register("string", stringMethods);
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
	public void execute(ConfigInfo config, Meta meta, ATextCompiler compiler, String[] args) throws CompileException {
		if (args.length==0) throw new CompileException("Empty method call");
		IMethod ameta = methods.get(args[0].toLowerCase());
		if (ameta==null) throw new CompileException("Unknown method " + args[0]);
		ameta.invoke( config, meta, compiler, args);
	}
	/**
	 * Register a meta method
	 * @param name - the command
	 * @param method - the method
	 */
	public static void register(String name, IMethod method) {
		methods.put(name,method);
	}
}