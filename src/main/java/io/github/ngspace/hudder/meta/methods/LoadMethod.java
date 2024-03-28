package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;

public class LoadMethod extends AMethod {

	@Override
	public void execute(ConfigInfo ci, Meta meta, ATextCompiler compiler, String... args) throws CompileException {
		String type = "load";
		try {
			type = args[0].toLowerCase();
			ATextCompiler ecompiler = (args.length>3 ? ci.getCompilerFromName(args[3]) : compiler);
			boolean AddText = (args.length>2 ? Boolean.valueOf(ecompiler.getVariable(args[2]).toString())
					: false) || type.equals("add");
			meta.combineWithResult(ecompiler.compile(ci, ci.getText(args[1])), AddText);
		} catch (CompileException e) {
			throw e;
		} catch (Exception e) {
			throw new CompileException("\""+type+"\" only accepts \""+type+",[file],<compiler>\"");
		}
	}
	
}
