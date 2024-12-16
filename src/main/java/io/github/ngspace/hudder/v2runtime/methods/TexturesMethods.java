package io.github.ngspace.hudder.v2runtime.methods;

import java.io.IOException;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.main.config.HudderConfig;
import io.github.ngspace.hudder.uielements.TextureElement;
import io.github.ngspace.hudder.utils.HudFileUtils;
import io.github.ngspace.hudder.utils.ObjectWrapper;
import net.minecraft.resources.ResourceLocation;

public class TexturesMethods implements IMethod {

	@Override public void invoke(HudderConfig i, CompileState m, ATextCompiler c, String type, int line, int charpos, ObjectWrapper... s) throws CompileException {
		switch (type) {
			case "image","png":
        		try {
        			boolean ex = HudFileUtils.exists(HudFileUtils.FOLDER + s[0].asString().trim());
        			if (!ex) return;
        			ResourceLocation id = ResourceLocation.withDefaultNamespace(s[0].asString().trim().toLowerCase());
					HudFileUtils.getAndRegisterImage(s[0].asString(),id);
	        		m.elements.add(new TextureElement(id,s[1].asInt(),s[2].asInt(),s[3].asInt(),s[4].asInt()));
				} catch (IOException e) {
					throw new CompileException(e.getLocalizedMessage(), line, charpos);
				}
				break;	
			case "texture":
				m.elements.add(new TextureElement(ResourceLocation.tryParse(s[0].asString()),s[1].asInt(),
		    			s[2].asInt(), s[3].asInt(),s[4].asInt()));
				break;
			default: throw new IllegalArgumentException();
		}
	}

}
