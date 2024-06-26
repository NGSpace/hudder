package io.github.ngspace.hudder.meta.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.meta.Meta;
import io.github.ngspace.hudder.meta.MetaCompiler.Value;
import io.github.ngspace.hudder.meta.elements.GameHudElement;
import io.github.ngspace.hudder.meta.elements.GameHudElement.GuiType;

public class GUIMethods implements IMethod {
	@Override
	public void invoke(ConfigInfo conf, Meta meta, ATextCompiler comp, String type, Value... s) throws CompileException {
		if (s.length!=2) throw new CompileException("\""+type+"\" only accepts \""+type+", [X], [Y]\"");
		int x = s[0].asInt();
		int y = s[1].asInt();
		GuiType guiType = switch (type) {
			case "health": yield GuiType.STATUS_BARS;
			case "xpbar": yield GuiType.EXP_AND_MOUNT_BAR;
			case "hotbar": yield GuiType.HOTBAR;
			case "helditemtooltip": yield GuiType.ITEM_TOOLTIP;
			default: throw new IllegalArgumentException("Unexpected value: " + type);
		};
		meta.elements.add(new GameHudElement(x, y, guiType));
	}
}