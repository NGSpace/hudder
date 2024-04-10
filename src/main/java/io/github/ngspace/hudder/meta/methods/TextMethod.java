package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;
import io.github.ngspace.hudder.meta.TextElement;

public class TextMethod extends AMethod {

	@Override
	public void invoke(ConfigInfo ci, Meta meta, ATextCompiler currcompiler, String... args) throws CompileException {
		try {
			ATextCompiler compiler = args.length>7 ? ci.getCompilerFromName(args[7]) : ci.getCompilerFromName("empty");
			
			
			String text = compiler.compile(ci, args[3]).TopLeftText;
			int txlen = text.length();
			float scale = (float) (args.length>4 ? tryParse(compiler.getVariable(args[4])) : ci.scale);

			Object textw = compiler.get("text_width");
			Object strw = compiler.get("string_width");
			int textwidth = Hudder.ins.textRenderer.getWidth(text);
			compiler.put("text_width", textwidth);
			compiler.put("string_width", textwidth);
			
			
			int x = tryParseInt(compiler.getVariable(args[1]));
			int y = tryParseInt(compiler.getVariable(args[2]));
			compiler.put("text_width", textw);
			compiler.put("string_width", strw);

			int color = args.length>5 ? tryParseInt(compiler.getVariable(args[5])) : ci.color;
			boolean shadow = args.length>6 ? Boolean.valueOf(String.valueOf(compiler.getVariable(args[6]))):ci.shadow;
			
			meta.elements.add(new TextElement(x,y,text.substring(txlen>1?1:0, txlen>1?txlen-1:0),scale,color,shadow));
		} catch (IndexOutOfBoundsException e) {
			throw new CompileException("\""+args[0]+"\" only accepts \""+args[0]+",[x],[y],[text],<scale>\"");
		} catch (Exception e) {throw new CompileException(e.getLocalizedMessage());}
	}

}
