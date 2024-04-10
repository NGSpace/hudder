package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;

public class DecimalMethod extends AMethod {
	@Override
	public void invoke(ConfigInfo ci, Meta meta, ATextCompiler compiler, String... args) throws CompileException {
		String type = "decimalpoint";
		try {
			type = args[0].toLowerCase();
			double point = tryParse(compiler.getVariable(args[2]));
			double d /*hehe double d*/ = tryParse(compiler.getVariable(args[1].trim()));
			double pow = Math.pow(10, point);
			compiler.put(args[1], ((int)(d * pow)) / pow);
		} catch (Exception e) {
			throw new CompileException("\""+type+"\" only accepts \""+type+",[Variable],[max decimal point]\"");
		}
	}
}
