package io.github.ngspace.hudder.methods.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.MethodValue;

public class DecimalMethods implements IMethod {

	@Override
	public boolean isDeprecated(String name) {
		return true;
	}
	@Override
	public String getDeprecationWarning(String name) {
		return "Use truncate function";
	}
	@Override
	public void invoke(ConfigInfo ci, CompileState meta, ATextCompiler comp, String type, int line, int charpos, MethodValue... args)
			throws CompileException {
		if (args.length!=2)
			throw new CompileException("\""+type+"\" only accepts ;"+type+",[Variable],[max decimal point];", line, charpos);
		double point = args[1].asDouble();
		double d /*hehe double d*/ = args[0].asDouble();
		double pow = Math.pow(10, point);
		comp.put(args[0].getAbsoluteValue(), ((int)(d * pow)) / pow);
	}
}
