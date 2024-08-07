package io.github.ngspace.hudder.compilers.hudderv2.runtime_elements;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.meta.Meta;

public class StringV2RuntimeElement extends AV2RuntimeElement {
	final String string;
	final boolean cleanUp;
	public StringV2RuntimeElement(String string, boolean cleanUp) {
		this.string = string;
		this.cleanUp = cleanUp;
	}
	@Override public void execute(Meta meta, StringBuilder builder) throws CompileException {
		
		if (cleanUp) {
			String str = string;
			int buffer = ConfigManager.getConfig().metaBuffer;
			if (buffer<10)
				for (int i = 0; i<buffer;i++) {
					if (str.startsWith("\n")||str.startsWith("\r")) str = str.substring(1);
				}
			builder.append(str);
			meta.addString(builder.toString(), false);
			builder.setLength(0);
		} else builder.append(string);
	}
}
