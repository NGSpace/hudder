package io.github.ngspace.hudder.v2runtime.runtime_elements;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;

public class StringV2RuntimeElement extends AV2RuntimeElement {
	final String string;
	final boolean cleanUp;
	final boolean addToMeta;
	int buffer;
	public StringV2RuntimeElement(String string, boolean cleanUp) {this(string,cleanUp,cleanUp);}
	public StringV2RuntimeElement(String string, boolean cleanUp, boolean add) {
		this.string = string;
		this.cleanUp = cleanUp;
		this.addToMeta = add;
	}
	@Override public boolean execute(CompileState meta, StringBuilder builder) throws CompileException {
		String str = string;
		if (cleanUp&&(buffer = Hudder.config.methodBuffer)<10)
			for (int i = 0; i<buffer;i++) {
				if (str.endsWith("\n")||str.endsWith("\r")) str = str.substring(0, str.length()-1);
			}
		
		if (addToMeta) {
			builder.append(str);
			meta.addString(builder.toString(), false);
			builder.setLength(0);
		} else builder.append(str);
		return true;
	}
}
