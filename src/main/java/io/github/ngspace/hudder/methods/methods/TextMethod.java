package io.github.ngspace.hudder.methods.methods;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.CompileState;
import io.github.ngspace.hudder.methods.MethodValue;
import io.github.ngspace.hudder.methods.elements.TextElement;

public class TextMethod implements IMethod {

	@Override
	public void invoke(ConfigInfo ci, CompileState meta, ATextCompiler comp, String type, MethodValue... args) throws CompileException {
		if (args.length<3) throw new CompileException(
				"\""+type+"\" only accepts ;"+type+",[x],[y],[text],<scale>,<color>,<shadow>,<bg>,<bgcolor>;");
		try {
			ATextCompiler compiler = args.length>8 ? Compilers.getCompilerFromName(args[8].getAbsoluteValue()) 
					: Compilers.getCompilerFromName("empty");
			

			int txlen = args[2].getAbsoluteValue().length();
			String text = compiler.compile(ci, args[2].getAbsoluteValue().substring(txlen>1?1:0, txlen>1?txlen-1:0))
					.TopLeftText;
			float scale = (float) (args.length>3 ? args[3].asDouble() : ci.scale);

			int textwidth = Hudder.ins.textRenderer.getWidth(text);
			compiler.put("text_width", textwidth);
			compiler.put("string_width", textwidth);
			compiler.put("strwidth", textwidth);
			
			
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