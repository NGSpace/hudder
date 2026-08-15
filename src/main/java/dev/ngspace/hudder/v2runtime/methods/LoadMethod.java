package dev.ngspace.hudder.v2runtime.methods;

import java.io.IOException;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.IUIElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
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
import net.minecraft.network.chat.Component;

public class LoadMethod implements V2IMethod, BindablePositionedConsumer {
	@Override
	public void invoke(HudderConfig ci, CompileState meta, AV2Compiler comp, V2Runtime runtime, String type,
			TextPos charpos, ObjectWrapper... args) throws ExecutionException {
		if (args.length<1)
			throw new ExecutionException("\""+type+"\" only accepts ;"+type+",[file],<text>,<compiler>;",
					charpos.line(), charpos.column());
		String file = null;
		try {
			file = args[0].asString();
		} catch (Exception _) {
			file = args[0].toString(); //Against my better judgement I've decided this is for the best...
			// 30/07/2026 Fuck that, my better judgement was better:
			if (HudCompilationManager.isFirstRunSinceCacheClear)
				Hudder.showWarningToast(Component.literal("Quote-less strings in " + type + " method are deprecated"), 
						Component.literal("The " + type + " method now requires quotes for strings like any other method"));
		}
		try {
			boolean AddText = (args.length<2 || args[1].asBoolean()) || type.equals("add");
			if (AddText && HudCompilationManager.isFirstRunSinceCacheClear)
				Hudder.showWarningToast(Component.literal("AddText parameter in the " + type + " method is deprecated"), 
						Component.literal("Please use the run function to read the text."
								+ (type.equals("add")?" Or use the run method instead.":"")));
			AHudCompiler<?> ecompiler=(args.length>2?Compilers.getCompilerFromName(args[2].asString()):comp);
			for (var i : HudCompilationManager.precomplistners) i.accept(ecompiler);
			CompileState state = runtime.getMasterScope().compileState;
			state.combineWithResult(ecompiler.processAndExecute(ci, file, file), AddText);
			for (var i : HudCompilationManager.postcomplistners) i.accept(ecompiler);
		} catch (IllegalArgumentException e) {
			throw new ExecutionException(e.getLocalizedMessage(), charpos.line(), charpos.column());
		} catch (CompileException e) {
			throw new ExecutionException(e.getFailureMessage() +"\nRun Failed for hud file " + file, charpos);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExecutionException(e);
		}
	}

	@Override
	public void invoke(IUIElementManager man, AHudCompiler<?> comp, TextPos pos, HudderConfig config, ObjectWrapper... args)
			throws ExecutionException {
		String file = args[0].asString();
		try {
			if (args.length>1&&args[1].asBoolean())
				throw new ExecutionException("V3 does not support the addText parameter!", pos);
			AHudCompiler<?> ecompiler=(args.length>2?Compilers.getCompilerFromName(args[2].asString()):comp);
			for (var i : HudCompilationManager.precomplistners) i.accept(ecompiler);
			for (var uielement : ecompiler.processAndExecute(config, file, file).elements()) {
				man.addUIElement(uielement);
			}
			for (var i : HudCompilationManager.postcomplistners) i.accept(ecompiler);
		} catch (IllegalArgumentException e) {
			throw new ExecutionException(e.getLocalizedMessage(), pos);
		} catch (CompileException e) {
			throw new ExecutionException(e.getFailureMessage() +"\nRun Failed for hud file " + file, pos);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExecutionException(e, pos);
		}
	}
}
