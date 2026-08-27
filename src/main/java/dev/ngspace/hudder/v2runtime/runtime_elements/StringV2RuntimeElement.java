package dev.ngspace.hudder.v2runtime.runtime_elements;

import dev.ngspace.hudder.api.compilers.CompileState;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;

public class StringV2RuntimeElement extends AV2RuntimeElement {
	
	public final String string;
	public final boolean cleanUp;
	public final boolean addToMeta;
	public final HudderConfig config;
	public int buffer;
	
	public StringV2RuntimeElement(String string, boolean cleanUp, HudderConfig config) {
		this(string, cleanUp, cleanUp, config);
	}
	public StringV2RuntimeElement(String string, boolean cleanUp, boolean add, HudderConfig config) {
		this.string = string;
		this.cleanUp = cleanUp;
		this.addToMeta = add;
		this.config = config;
	}
	@Override public boolean execute(CompileState meta, StringBuilder builder) throws ExecutionException {
		String str = string;
		if (cleanUp&&(buffer = config.methodBuffer())<10) {
			for (int i = 0; i<buffer;i++) {
				if (str.endsWith("\n")||str.endsWith("\r")) str = str.substring(0, str.length()-1);
			}
		}
		builder.append(str);
		if (addToMeta) {
			meta.addString(builder.toString(), false);
			builder.setLength(0);
		}
		return true;
	}
}
