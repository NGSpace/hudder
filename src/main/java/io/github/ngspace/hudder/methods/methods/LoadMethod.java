package io.github.ngspace.hudder.methods.methods;

import java.io.IOException;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.util.HudCompilationManager;
import io.github.ngspace.hudder.util.HudFileUtils;
import io.github.ngspace.hudder.util.ObjectWrapper;

public class LoadMethod implements IMethod {
	@Override
	public void invoke(ConfigInfo ci, CompileState meta, ATextCompiler comp, String type, int line, int charpos, ObjectWrapper... args) throws CompileException {
		if (args.length<1)
			throw new CompileException("\""+type+"\" only accepts ;"+type+",[file],<text>,<compiler>;");
		String file = null;
		try {
			file = args[0].asString();
		} catch (Exception e) {
			file = args[0].toString(); //Against my better judgement I've decided this is for the best...
		}
		try {
			boolean AddText = (args.length<2 || args[1].asBoolean()) || type.equals("add");
			ATextCompiler ecompiler=(args.length>2?Compilers.getCompilerFromName(args[2].asString()):comp);
			for (var i : HudCompilationManager.precomplistners) i.accept(ecompiler);
			meta.combineWithResult(ecompiler.compile(ci, HudFileUtils.getFile(file), file), AddText);
			for (var i : HudCompilationManager.postcomplistners) i.accept(ecompiler);
		} catch (ReflectiveOperationException | IOException e) {
			throw new CompileException(e.getLocalizedMessage());
		} catch (CompileException e) {
			throw new CompileException(e.getFailureMessage() +"\nRun Failed for hud file " + file, line, charpos);
		}
	}
}