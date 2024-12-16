package io.github.ngspace.hudder.v2runtime.methods;

import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileState;
import io.github.ngspace.hudder.main.config.HudderConfig;
import io.github.ngspace.hudder.uielements.GameHudElement;
import io.github.ngspace.hudder.uielements.GameHudElement.GuiType;
import io.github.ngspace.hudder.utils.ObjectWrapper;

public class GUIMethods implements IMethod {
	@Override
	public void invoke(HudderConfig conf, CompileState meta, ATextCompiler comp, String type, int line, int charpos, ObjectWrapper... s) throws CompileException {
		if (s.length!=2) throw new CompileException("\""+type+"\" only accepts ;"+type+", [X], [Y];", line, charpos);
		int x = s[0].asInt();
		int y = s[1].asInt();
		GuiType guiType = switch (type) {
			case "health", "statusbars": yield GuiType.STATUS_BARS;
			case "xpbar": yield GuiType.EXP_AND_MOUNT_BAR;
			case "hotbar": yield GuiType.HOTBAR;
			case "helditemtooltip": yield GuiType.ITEM_TOOLTIP;
			default: throw new IllegalArgumentException("Unexpected value: " + type);
		};
		meta.elements.add(new GameHudElement(x, y, guiType));
	}
}