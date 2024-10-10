package io.github.ngspace.hudder.v2runtime.functions;

import java.util.HashMap;
import java.util.Map;

public class V2FunctionHandler {
	private Map<String, AV2Function> functions = new HashMap<String,AV2Function>();
	
	public V2FunctionHandler() {
		register(new TestFunction(), "test");
	}

	public void register(AV2Function function, String... names) {
		for(String name:names) functions.put(name,function);
	}
	
	public AV2Function getFunction(String name) {
		return functions.get(name);
	}
}
