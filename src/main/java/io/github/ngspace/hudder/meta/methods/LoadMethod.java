package io.github.ngspace.hudder.meta.methods;

import java.io.IOException;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.CompileState;
import io.github.ngspace.hudder.meta.MethodValue;
import io.github.ngspace.hudder.util.HudFileUtils;

public class LoadMethod implements IMethod {
	@Override
	public void invoke(ConfigInfo ci, CompileState meta, ATextCompiler comp, String type, MethodValue... args) throws CompileException {
		if (args.length<1)
			throw new CompileException("\""+type+"\" only accepts \""+type+",[file],<text>,<compiler>\"");
		try {
			ATextCompiler ecompiler=(args.length>2?Compilers.getCompilerFromName(args[2].getAbsoluteValue()):comp);
			boolean AddText = (args.length<2 || args[1].asBoolean()) || type.equals("add");
			meta.combineWithResult(ecompiler.compile(ci, HudFileUtils.getFile(args[0].getAbsoluteValue())), AddText);
		} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException | IOException e) {
			throw new CompileException(e.getLocalizedMessage());
		}
	}
}
