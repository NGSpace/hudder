package io.github.ngspace.hudder.methods.methods;

import io.github.ngspace.hudder.compilers.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.CompileState;
import io.github.ngspace.hudder.methods.MethodValue;
import io.github.ngspace.hudder.methods.elements.GameHudElement;
import io.github.ngspace.hudder.methods.elements.GameHudElement.GuiType;

public class GUIMethods implements IMethod {
	@Override
	public void invoke(ConfigInfo conf, CompileState meta, ATextCompiler comp, String type, MethodValue... s) throws CompileException {
		if (s.length!=2) throw new CompileException("\""+type+"\" only accepts ;"+type+", [X], [Y];");
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