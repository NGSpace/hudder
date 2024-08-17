package io.github.ngspace.hudder.meta.methods;

import java.io.IOException;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.CompileState;
import io.github.ngspace.hudder.meta.MethodValue;
import io.github.ngspace.hudder.meta.elements.TextureElement;
import io.github.ngspace.hudder.util.HudFileUtils;
import net.minecraft.util.Identifier;

public class TexturesMethods implements IMethod {

	@Override public void invoke(ConfigInfo i, CompileState m, ATextCompiler c, String type, MethodValue... s) throws CompileException {
		switch (type) {
			case "image","png":
        		try {
        			boolean ex = HudFileUtils.exists(HudFileUtils.FOLDER + s[0].asStringSafe().trim());
        			if (!ex) return;
            		Identifier id = Identifier.of(s[0].asStringSafe().trim().toLowerCase());
					HudFileUtils.getAndRegisterImage(s[0].asStringSafe(),id);
	        		m.elements.add(new TextureElement(id,s[1].asInt(),s[2].asInt(),s[3].asInt(),s[4].asInt()));
				} catch (IOException e) {
					throw new CompileException(e.getLocalizedMessage());
				}
				break;	
			case "texture":
				m.elements.add(new TextureElement(Identifier.tryParse(s[0].asString()),s[1].asInt(),
		    			s[2].asInt(), s[3].asInt(),s[4].asInt()));
				break;
			default: throw new IllegalArgumentException();
		}
	}

}
