package io.github.ngspace.hudder.compilers.v2runtime;

import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.compilers.AVarTextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.compilers.v2runtime.values.V2Value;
import io.github.ngspace.hudder.compilers.v2runtime.values.V2Values;
import io.github.ngspace.hudder.config.ConfigInfo;

public abstract class AV2Compiler extends AVarTextCompiler {
	
	Map<String, V2Runtime> runtimes = new HashMap<String, V2Runtime>();
	
	public V2Value getV2Value(String string) {
		return V2Values.of(string, this);
	}
	@Override
	public CompileResult compile(ConfigInfo info, String text) throws CompileException {
		V2Runtime runtime = runtimes.get(text);
		if (runtime==null) runtimes.put(text, (runtime=buildRuntime(info,text)));
		return runtime.execute().toResult();
	}
	public abstract V2Runtime buildRuntime(ConfigInfo info, String text) throws CompileException;
}
