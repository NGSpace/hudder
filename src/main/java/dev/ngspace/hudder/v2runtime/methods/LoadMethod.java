package dev.ngspace.hudder.v2runtime.methods;

import dev.ngspace.hudder.compilers.abstractions.AHudCompiler;
import dev.ngspace.hudder.compilers.abstractions.AV2Compiler;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.compilers.utils.Compilers;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.main.HudCompilationManager;
import dev.ngspace.hudder.utils.ObjectWrapper;
import dev.ngspace.hudder.v2runtime.V2Runtime;

public class LoadMethod implements V2IMethod {
	@Override
	public void invoke(HudderConfig ci, CompileState meta, AV2Compiler comp, V2Runtime runtime, String type,
			TextPos charpos, ObjectWrapper... args) throws ExecutionException {
		if (args.length<1)
			throw new ExecutionException("\""+type+"\" only accepts ;"+type+",[file],<text>,<compiler>;",
					charpos.line(), charpos.column());
		String file = null;
		try {
			file = args[0].asString();
		} catch (Exception e) {
			file = args[0].toString(); //Against my better judgement I've decided this is for the best...
		}
		try {
			boolean AddText = (args.length<2 || args[1].asBoolean()) || type.equals("add");
			AHudCompiler<?> ecompiler=(args.length>2?Compilers.getCompilerFromName(args[2].asString()):comp);
			for (var i : HudCompilationManager.precomplistners) i.accept(ecompiler);
			CompileState state = runtime.getMasterScope().compileState;
			state.combineWithResult(ecompiler.processAndExecute(ci, file, file), AddText);
			for (var i : HudCompilationManager.postcomplistners) i.accept(ecompiler);
		} catch (IllegalArgumentException e) {
			throw new ExecutionException(e.getLocalizedMessage(), charpos.line(), charpos.column());
		} catch (CompileException e) {
			throw new ExecutionException(e.getFailureMessage() +"\nRun Failed for hud file " + file, charpos);
		}
	}
}
