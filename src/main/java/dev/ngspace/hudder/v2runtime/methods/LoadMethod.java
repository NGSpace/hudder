package dev.ngspace.hudder.v2runtime.methods;

import java.io.IOException;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.compilers.AV2Compiler;
import dev.ngspace.hudder.api.compilers.utils.CompileState;
import dev.ngspace.hudder.api.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.utils.ObjectWrapper;
import dev.ngspace.hudder.v2runtime.V2Runtime;
import net.minecraft.network.chat.Component;

public class LoadMethod implements V2IMethod {
	@Override
	public void invoke(HudderConfig config, CompileState meta, AV2Compiler comp, V2Runtime runtime, String type,
			TextPos charpos, ObjectWrapper... args) throws ExecutionException {
		if (args.length<1)
			throw new ExecutionException("\""+type+"\" only accepts ;"+type+",[file],<text>,<compiler>;",
					charpos.line(), charpos.column());
		String file = null;
		try {
			file = args[0].asString();
		} catch (Exception e) {
			e.printStackTrace();
			file = args[0].toString(); //Against my better judgement I've decided this is for the best...
			// 30/07/2026 Fuck that, my better judgement was better:
			if (config.compilationManager.isFirstRunSinceCacheClear)
				Hudder.showWarningToast(Component.literal("Quote-less strings in " + type + " method are deprecated"), 
						Component.literal("The " + type + " method now requires quotes for strings like any other method"));
		}
		try {
			boolean AddText = (args.length>2 && args[1].asBoolean()) || type.equals("add");
			AHudCompiler<?> compiler=(args.length>2?config.registry.findEntryFromName(args[2].asString())
					.orElseThrow(()->new ExecutionException("Compiler not found", charpos)).compiler():comp);
			CompileState state = runtime.getMasterScope().compileState;
			state.combineWithResult(config.compilationManager.compileAndExecuteSecondaryHud(compiler, file, file), AddText);
		} catch (IllegalArgumentException e) {
			throw new ExecutionException(e.getLocalizedMessage(), charpos.line(), charpos.column());
		} catch (CompileException e) {
			throw new ExecutionException(e.getFailureMessage() +"\nRun Failed for hud file " + file, charpos);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExecutionException(e);
		}
	}
}
