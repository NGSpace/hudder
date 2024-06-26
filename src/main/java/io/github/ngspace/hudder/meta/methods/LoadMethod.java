package io.github.ngspace.hudder.meta.methods;

import java.io.IOException;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;
import io.github.ngspace.hudder.meta.MetaCompiler.Value;
import io.github.ngspace.hudder.util.HudFileUtils;

public class LoadMethod implements IMethod {
	@Override
	public void invoke(ConfigInfo ci, Meta meta, ATextCompiler comp, String type, Value... args) throws CompileException {
		if (args.length<1)
			throw new CompileException("\""+type+"\" only accepts \""+type+",[file],<text>,<compiler>\"");
		try {
			ATextCompiler ecompiler=(args.length>2?ATextCompiler.getCompilerFromName(args[2].getAbsoluteValue()):comp);
			boolean AddText = (args.length<2 || args[1].asBoolean()) || type.equals("add");
			meta.combineWithResult(ecompiler.compile(ci, HudFileUtils.getFile(args[0].getAbsoluteValue())), AddText);
		} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException | IOException e) {
			throw new CompileException(e.getLocalizedMessage());
		}
	}
}
