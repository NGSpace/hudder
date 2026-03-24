package dev.ngspace.hudder.v2runtime.runtime_elements;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.utils.CompileState;
import dev.ngspace.hudder.exceptions.ExecutionException;

public class StringV2RuntimeElement extends AV2RuntimeElement {
	public final String string;
	public final boolean cleanUp;
	public final boolean addToMeta;
	public int buffer;
	public StringV2RuntimeElement(String string, boolean cleanUp) {this(string,cleanUp,cleanUp);}
	public StringV2RuntimeElement(String string, boolean cleanUp, boolean add) {
		this.string = string;
		this.cleanUp = cleanUp;
		this.addToMeta = add;
	}
	@Override public boolean execute(CompileState meta, StringBuilder builder) throws ExecutionException {
		String str = string;
		if (cleanUp&&(buffer = Hudder.config.methodBuffer())<10) {
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
