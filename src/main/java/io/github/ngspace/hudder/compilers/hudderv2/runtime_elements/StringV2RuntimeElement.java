package io.github.ngspace.hudder.compilers.hudderv2.runtime_elements;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.meta.CompileState;

public class StringV2RuntimeElement extends AV2RuntimeElement {
	final String string;
	final boolean cleanUp;
	final boolean add;
	public StringV2RuntimeElement(String string, boolean cleanUp) {this(string,cleanUp,cleanUp);}
	public StringV2RuntimeElement(String string, boolean cleanUp, boolean add) {
		this.string = string;
		this.cleanUp = cleanUp;
		this.add = add;
	}
	@Override public void execute(CompileState meta, StringBuilder builder) throws CompileException {

		String str = string;
		if (cleanUp) {
			int buffer = ConfigManager.getConfig().methodBuffer;
			if (buffer<10)
				for (int i = 0; i<buffer;i++) {
					if (str.endsWith("\n")||str.endsWith("\r")) str = str.substring(0, str.length()-1);
				}
		}
		if (add) {
			builder.append(str);
			meta.addString(builder.toString(), false);
			builder.setLength(0);
		} else builder.append(str);
	}
}
