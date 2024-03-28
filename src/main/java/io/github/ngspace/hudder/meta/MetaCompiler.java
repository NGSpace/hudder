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
	public Map<String, IMethod> methods = new HashMap<String,IMethod>();
	public MetaCompiler() {
		var itemstackmethod = new ItemStackMethods();
		methods.put("slot", itemstackmethod);
		methods.put("item", itemstackmethod);
		methods.put("hand", itemstackmethod);
		methods.put("selectedslot", itemstackmethod);
		methods.put("hat", itemstackmethod);
		methods.put("helmet", itemstackmethod);
		methods.put("chestplate", itemstackmethod);
		methods.put("leggings", itemstackmethod);
		methods.put("pants", itemstackmethod);
		methods.put("boots", itemstackmethod);
		methods.put("offhand", itemstackmethod);
		IMethod v=(ci,meta,compiler,args)->
			meta.setTextLocation(args[0],(float) (args.length>1?tryParse(args[1]):ci.scale));
		methods.put(BOTTOMRIGHT, v);
		methods.put(TOPLEFT, v);
		methods.put(TOPRIGHT, v);
		methods.put(BOTTOMLEFT, v);
		methods.put(MUTE, v);
		var loadMethod = new LoadMethod();
		methods.put("load", loadMethod);
		methods.put("execute", loadMethod);
		methods.put("compile", loadMethod);
		methods.put("run", loadMethod);
		methods.put("add", loadMethod);
		var textMethods = new TextMethod();
		methods.put("text", textMethods);
		IMethod s = (ci,meta,comp,args) -> {
			try {
				comp.put(args[1],(int)tryParse(comp.getVariable(args[1].trim())));
			} catch (IndexOutOfBoundsException e) {
				throw new CompileException("\""+args[0]+"\" only accepts \""+args[0]+",[Variable]\"");
			}
		};
		methods.put("int",s);
		methods.put("fullnumber",s);
		var decimalMethods = new DecimalMethod();
		methods.put("decimalpoint", decimalMethods);
		var stringMethods = new StringMethods();
		methods.put("concat", stringMethods);
		methods.put("multiplystring", stringMethods);
		methods.put("substring", stringMethods);
		methods.put("string", stringMethods);
	}
	public String execute(ConfigInfo ci, Meta meta, ATextCompiler compiler, String[] args) throws CompileException {
		if (args.length==0) throw new CompileException("Empty method call");
		IMethod ameta = methods.get(args[0].toLowerCase());
		if (ameta==null) throw new CompileException("Unknown method " + args[0]);
		ameta.execute(ci, meta, compiler, args);
		return "";
	}
}