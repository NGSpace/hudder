package io.github.ngspace.hudder.compilers;

import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.config.ConfigInfo;

public class UnaccessableTestCompiler extends ATextCompiler {

	@Override public CompileResult compile(ConfigInfo info, String text) throws CompileException {
		return CompileResult.of("HOW DID YOU FIND OUT I AM NOT INACCESSABLE?!?!");
	}

	@Override public Object getVariable(String key) throws CompileException {
		//I made a typo... I am keeping this here...
		return "Advanchment unlocked: How did we get here?";
	}

	@Override public void reset() {/*Do nothin*/}
}
