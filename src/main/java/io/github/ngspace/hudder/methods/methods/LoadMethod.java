package io.github.ngspace.hudder.methods.methods;

import java.io.IOException;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.MethodValue;
import io.github.ngspace.hudder.util.HudFileUtils;

public class LoadMethod implements IMethod {
	@Override
	public void invoke(ConfigInfo ci, CompileState meta, ATextCompiler comp, String type, MethodValue... args) throws CompileException {
		if (args.length<1)
			throw new CompileException("\""+type+"\" only accepts ;"+type+",[file],<text>,<compiler>;");
		try {
			boolean AddText = (args.length<2 || args[1].asBoolean()) || type.equals("add");
			ATextCompiler ecompiler=(args.length>2?Compilers.getCompilerFromName(args[2].asStringSafe()):comp);
			for (var i : Hudder.precomplistners) i.accept(ecompiler);
			meta.combineWithResult(ecompiler.compile(ci, HudFileUtils.getFile(args[0].asStringSafe())), AddText);
			for (var i : Hudder.postcomplistners) i.accept(ecompiler);
		} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException | IOException e) {
			throw new CompileException(e.getLocalizedMessage());
		}
	}
}
