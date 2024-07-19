package io.github.ngspace.hudder.compilers.hudderv2.runtime_elements;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.compilers.hudderv2.HudderV2Runtime;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;
import io.github.ngspace.hudder.meta.MetaCompiler;

public class MetaV2RuntimeElement extends AV2RuntimeElement {
	
	private String[] args;
	private HudderV2Runtime runtime;
	private ConfigInfo info;
	
	public final MetaCompiler metacomp = new MetaCompiler();

	public MetaV2RuntimeElement(String[] metabuilder, HudderV2Runtime runtime, ConfigInfo info) {
		this.args = metabuilder;
		this.runtime = runtime;
		this.info = info;
	}

	@Override public void execute(Meta meta, StringBuilder builder) throws CompileException {
		String command = args[0].toLowerCase();
//		if (command.equals(Meta.TOPLEFT)
//				||command.equals(Meta.BOTTOMLEFT)
//				||command.equals(Meta.TOPRIGHT)
//				||command.equals(Meta.BOTTOMRIGHT)) {
//			meta.addString(args[1], true);
//		}
//		if (command.equals(Meta.MUTE)) meta.addString("", true);
		metacomp.execute(info, meta, runtime.compiler, args);
	}
}
