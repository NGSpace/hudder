package dev.ngspace.hudder.api.compilers.compilers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.interfaces.HudEvaluator;
import dev.ngspace.hudder.api.compilers.interfaces.PreparedCompiler;
import dev.ngspace.hudder.api.compilers.utils.HudInformation;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.uielements.AUIElement;
import dev.ngspace.hudder.utils.HudFileUtils;

public abstract class AScriptingLanguageCompiler extends AHudCompiler<IScriptingLanguageEngine> implements
		PreparedCompiler, HudEvaluator<IScriptingLanguageEngine> {
	
	public ArrayElementManager elms = new ArrayElementManager();
	
	protected AScriptingLanguageCompiler(HudderConfig config) {
		super(config, new HashMap<>());
	}
	
	protected abstract IScriptingLanguageEngine createLangEngine() throws CompileException;

	@Override
	public IScriptingLanguageEngine processFile(Path filepath) throws CompileException, IOException {
		String text = HudFileUtils.readFile(filepath);
		return evalHud(text, filepath.getFileName().toString());
	}
	
	@Override
	public IScriptingLanguageEngine evalHud(String text, String debugname) throws CompileException {
		IScriptingLanguageEngine wrapper = null;
		try {
			wrapper = createLangEngine();
			
			try {
				wrapper.evaluateCode(text, debugname);
			} catch (Exception e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				wrapper.close();
				throw wrapper.processCompileException(e);
			}
			return wrapper;
		} catch (Exception e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			if (wrapper!=null) {
				throw wrapper.processCompileException(e);
			} 
			if (e instanceof RuntimeException ex) throw ex;
			throw new CompileException(e.getMessage(),-1,-1,e);
		}
		
	}

	@Override public HudInformation execute(IScriptingLanguageEngine wrapper, String filename)
			throws ExecutionException {
		try {
			String TL = String.valueOf(wrapper.callFunctionSafe("topleft", ""));
			String BL = String.valueOf(wrapper.callFunctionSafe("bottomleft", ""));
			String TR = String.valueOf(wrapper.callFunctionSafe("topright", ""));
			String BR = String.valueOf(wrapper.callFunctionSafe("bottomright", ""));
			
			wrapper.callFunctionSafe("createElements", null);
			
			/* Scale */
			
			float TLscale = wrapper.readVariableSafe("topleftscale",1f).asFloat();
			float BLscale = wrapper.readVariableSafe("bottomleftscale",1f).asFloat();
			float TRscale = wrapper.readVariableSafe("toprightscale",1f).asFloat();
			float BRscale = wrapper.readVariableSafe("bottomrightscale",1f).asFloat();
			
			return new HudInformation(TL, TLscale, BL, BLscale, TR, TRscale, BR, BRscale,
					elms.toArray(new AUIElement[elms.size()]));
		} catch (Exception e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			if (wrapper!=null) {
				throw wrapper.processException(e);
			} 
			if (e instanceof RuntimeException ex) throw ex;
			throw new ExecutionException(e.getMessage(),-1,-1,e);
		}
	}
	
	@Override
	public void reset() throws IOException {
		for(IScriptingLanguageEngine c:instances.values()) c.close();
		super.reset();
	}
	
	@Override
	public void prepareCompiler() {
		elms.clear();
	}
	
	@Override
	public HudInformation evalAndExecuteHud(String text, String debugname) throws CompileException, ExecutionException {
		return execute(evalHud(text, debugname), debugname);
	}
}
