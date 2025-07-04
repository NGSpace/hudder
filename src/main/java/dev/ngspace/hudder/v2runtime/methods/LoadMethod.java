package dev.ngspace.hudder.v2runtime.methods;

import dev.ngspace.hudder.compilers.abstractions.ATextCompiler;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.compilers.utils.Compilers;
import dev.ngspace.hudder.main.HudCompilationManager;
import dev.ngspace.hudder.main.config.HudderConfig;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.utils.ObjectWrapper;

public class LoadMethod implements IMethod {
	@Override
	public void invoke(HudderConfig ci, CompileState meta, ATextCompiler comp, String type, int line, int charpos, ObjectWrapper... args) throws CompileException {
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
			meta.combineWithResult(ecompiler.compile(ci, HudFileUtils.readFile(file), file), AddText);
			for (var i : HudCompilationManager.postcomplistners) i.accept(ecompiler);
		} catch (ReflectiveOperationException e) {
			throw new CompileException(e.getLocalizedMessage());
		} catch (CompileException e) {
			throw new CompileException(e.getFailureMessage() +"\nRun Failed for hud file " + file, line, charpos);
		}
	}
}
