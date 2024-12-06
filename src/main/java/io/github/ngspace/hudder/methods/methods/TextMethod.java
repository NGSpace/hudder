package io.github.ngspace.hudder.methods.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.elements.TextElement;
import io.github.ngspace.hudder.utils.ObjectWrapper;

public class TextMethod implements IMethod {

	@Override
	public void invoke(ConfigInfo ci, CompileState meta, ATextCompiler comp, String type, int line, int charpos, ObjectWrapper... args) throws CompileException {
		try {
			String text = args[2].asString();
			float scale = (float) (args.length>3 ? args[3].asDouble() : ci.scale);
			
			int x = args[0].asInt();
			int y = args[1].asInt();

			int color = args.length>4 ? args[4].asInt() : ci.color;
			boolean shadow = args.length>5 ? args[5].asBoolean():ci.shadow;
			boolean bg = args.length>6 ? args[6].asBoolean():ci.background;
			double bgcolor = args.length>7 ? args[7].asDouble() : ci.backgroundcolor;
			
			meta.elements.add(new TextElement(x,y,text,scale,color,shadow,bg,(long) bgcolor));
		} catch (Exception e) {throw new CompileException(e.getLocalizedMessage(), line, charpos);}
	}

}
