package io.github.ngspace.hudder.v2runtime;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.MethodHandler;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;
import io.github.ngspace.hudder.v2runtime.values.V2ValueParser;

public abstract class AV2Compiler extends AVarTextCompiler {
	
	protected AV2Compiler() {
		Hudder.addPreCompilerListener(c -> {if (this==c) tempVariables.clear();});
	}
	
	public Map<String, V2Runtime> runtimes = new HashMap<String, V2Runtime>();
	public final MethodHandler methodHandler = new MethodHandler();
	
	protected Map<String, Object> tempVariables = new HashMap<String, Object>();
	
	public void setTempVariable(String key, Object value) {tempVariables.put(key, value);}
	public Object getTempVariable(String key) {return tempVariables.get(key);}
	
	public AV2Value getV2Value(V2Runtime runtime, String string, int line, int charpos) throws CompileException {
		return V2ValueParser.of(runtime, string, this, line, charpos);
	}
	
	@Override public final CompileResult compile(ConfigInfo info, String text) throws CompileException {
		V2Runtime runtime = runtimes.get(text);
		if (runtime==null) runtimes.put(text, (runtime=buildRuntime(info,text, new CharPosition(-1, -1))));
		return runtime.execute().toResult();
	}
	public abstract V2Runtime buildRuntime(ConfigInfo info, String text, CharPosition charPosition) throws CompileException;
	
	public void putTemp(String key, Object value) {
		tempVariables.put(key, value);
	}
}
