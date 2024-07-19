package io.github.ngspace.hudder.compilers.hudderv2.runtime_elements;

import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.meta.Meta;

public class StringV2RuntimeElement extends AV2RuntimeElement {
	final String string;
	final boolean cleanUp;
	public StringV2RuntimeElement(String string, boolean cleanUp) {
		this.string = string;
		this.cleanUp = cleanUp;
	}
	@Override public void execute(Meta meta, StringBuilder builder) throws CompileException {
		builder.append(string);
		if (cleanUp) {meta.addString(builder.toString(), cleanUp);builder.setLength(0);}
	}
}
