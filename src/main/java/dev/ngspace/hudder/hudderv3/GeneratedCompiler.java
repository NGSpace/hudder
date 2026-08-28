package dev.ngspace.hudder.hudderv3;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import dev.ngspace.hudder.api.compilers.compilers.AHudCompiler;
import dev.ngspace.hudder.api.compilers.compilers.AV3Compiler;
import dev.ngspace.hudder.api.compilers.interfaces.PreparedCompiler;
import dev.ngspace.hudder.api.compilers.interfaces.VariablesManager;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;

public abstract class GeneratedCompiler extends AHudCompiler<String> implements PreparedCompiler,
		VariablesManager {
	
	public HudderV3Helper helper;
	public ArrayElementManager uimanager;
	public AV3Compiler v3compiler;
	
	protected Map<String, Object> tempVariables = new HashMap<String, Object>();
	protected Map<String, Object> variables = new HashMap<String, Object>();
	
	protected GeneratedCompiler(AV3Compiler compiler, HudderV3Helper helper) {
		super(helper.config, new AtomicReference<>(), new HashMap<>());
		this.helper = helper;
		this.v3compiler = compiler;
		this.uimanager = new ArrayElementManager();
	}

	public abstract V3HudInformation execute(HudderConfig config, String filename) throws ExecutionException;
	
	@Override
	public void reset() throws IOException {
		tempVariables.clear();
		variables.clear();
		super.reset();
	}
	
	@Override
    public void prepareCompiler() {
		tempVariables.clear();
    }
	
	@Override
	public void putVariable(String key, Object value) {
		variables.put(key, value);
	}
	
	@Override
    public Object getVariable(String key) throws ExecutionException {
		return variables.get(key);
    }
}
