package io.github.ngspace.hudder.compilers.hudderv2.runtime_elements;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.hudderv2.V2Runtime;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;
import io.github.ngspace.hudder.meta.MetaCompiler;

public class MetaV2RuntimeElement extends AV2RuntimeElement {
	
	private String[] args;
	private V2Runtime runtime;
	private ConfigInfo info;
	
	public final MetaCompiler metacomp = new MetaCompiler();

	public MetaV2RuntimeElement(String[] metabuilder, V2Runtime runtime, ConfigInfo info) {
		this.args = metabuilder;
		this.runtime = runtime;
		this.info = info;
	}

	@Override public void execute(Meta meta, StringBuilder builder) throws CompileException {
		metacomp.execute(info, meta, runtime.compiler, args);
	}
}
