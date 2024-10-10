package io.github.ngspace.hudder.v2runtime;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.v2runtime.values.AV2Value;
import io.github.ngspace.hudder.v2runtime.values.V2ValueParser;

public abstract class AV2Compiler extends AVarTextCompiler {
	
	public Map<String, V2Runtime> runtimes = new HashMap<String, V2Runtime>();
	
	public AV2Value getV2Value(V2Runtime runtime, String string) throws CompileException {
		return V2ValueParser.of(runtime, string, this);
	}
	
	@Override public CompileResult compile(ConfigInfo info, String text) throws CompileException {
		V2Runtime runtime = runtimes.get(text);
		if (runtime==null) runtimes.put(text, (runtime=buildRuntime(info,text)));
		return runtime.execute().toResult();
	}
	public abstract V2Runtime buildRuntime(ConfigInfo info, String text) throws CompileException;
	
	@Override
	public void reset() {runtimes.clear();variables.clear();}
}
