package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;
import io.github.ngspace.hudder.meta.TextElement;

public class TextMethod extends AMethod {

	@Override
	public void execute(ConfigInfo ci, Meta meta, ATextCompiler compiler, String... args) throws CompileException {
		try {
			ATextCompiler compilername = ci.getCompilerFromName(args.length>5 ? 
					args[5] : "Default");
			
			String text = compilername.compile(ci,args[3]).TopLeftText;
			int txlen = text.length();
			float scale = (float) (args.length>4 ? tryParse(compilername.getVariable(args[4])) : ci.scale);

			var textw = compilername.get("text_width");
			var strw = compilername.get("string_width");
			int textwidth = Hudder.ins.textRenderer.getWidth(text);
			compilername.put("text_width", textwidth);
			compilername.put("string_width", textwidth);
			
			
			double x = tryParse(compilername.getVariable(args[1]));
			double y = tryParse(compilername.getVariable(args[2]));
			compilername.put("text_width", textw);
			compilername.put("string_width", strw);
			
			meta.elements.add(new TextElement(x,y,text.substring(txlen>1?1:0, txlen>1?txlen-1:0),scale,ci));
		} catch (Exception e) {
			throw new CompileException("\""+args[0]+"\" only accepts \""+args[0]+",[x],[y],[text],<scale>\"");
		}
	}

}
