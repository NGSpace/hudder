package io.github.ngspace.hudder.methods.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.MethodValue;
import io.github.ngspace.hudder.methods.elements.TextElement;

public class TextMethod implements IMethod {

	@Override
	public void invoke(ConfigInfo ci, CompileState meta, ATextCompiler comp, String type, MethodValue... args) throws CompileException {
		if (args.length<3) throw new CompileException(
				"\""+type+"\" only accepts ;"+type+",[x],[y],[text],<scale>,<color>,<shadow>,<bg>,<bgcolor>;");
		try {
			String text = args[2].asString();
			float scale = (float) (args.length>3 ? args[3].asDouble() : ci.scale);
			
			int x = args[0].asInt();
			int y = args[1].asInt();

			int color = args.length>4 ? args[4].asInt() : ci.color;
			boolean shadow = args.length>5 ? args[5].asBoolean():ci.shadow;
			int bgcolor = args.length>7 ? args[7].asInt() : ci.backgroundcolor;
			boolean bg = args.length>6 ? args[6].asBoolean():ci.background;
			
			meta.elements.add(new TextElement(x,y,text,scale,color,shadow,bg,bgcolor));
		} catch (Exception e) {throw new CompileException(e.getLocalizedMessage());}
	}

}
