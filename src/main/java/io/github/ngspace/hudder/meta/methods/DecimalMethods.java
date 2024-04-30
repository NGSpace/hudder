package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;
import io.github.ngspace.hudder.meta.MetaCompiler.Value;

public class DecimalMethods implements IMethod {
	@Override
	public void invoke(ConfigInfo ci, Meta meta, ATextCompiler comp, String type, Value... args) throws CompileException {
		if (args.length!=2)
			throw new CompileException("\""+type+"\" only accepts \""+type+",[Variable],[max decimal point]\"");
		double point = args[1].asDouble();
		double d /*hehe double d*/ = args[0].asDouble();
		double pow = Math.pow(10, point);
		comp.put(args[0].getAbsoluteValue(), ((int)(d * pow)) / pow);
	}
}
