package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.CompileState;
import io.github.ngspace.hudder.meta.MethodValue;

public class DecimalMethods implements IMethod {
	@Override
	public void invoke(ConfigInfo ci, CompileState meta, ATextCompiler comp, String type, MethodValue... args)
			throws CompileException {
		if (args.length!=2)
			throw new CompileException("\""+type+"\" only accepts ;"+type+",[Variable],[max decimal point];");
		double point = args[1].asDouble();
		double d /*hehe double d*/ = args[0].asDouble();
		double pow = Math.pow(10, point);
		comp.put(args[0].asStringSafe(), ((int)(d * pow)) / pow);
	}
}
