package dev.ngspace.ngsmcconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.ngspace.hudder.Hudder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

public class NGSMCConfigEntry extends ContainerObjectSelectionList.Entry<NGSMCConfigEntry>{
	
	List<AbstractWidget> children = new ArrayList<AbstractWidget>();

	@Override
	public List<? extends GuiEventListener> children() {
		return children;
	}

	@Override
	public List<? extends NarratableEntry> narratables() {
		return Collections.emptyList();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight,
			int mouseX, int mouseY, boolean hovered, float delta) {
		Hudder.log(x);
		guiGraphics.fill(0, y, x+entryWidth, y+entryHeight, 0xFFFF0000);
		for (var child : children) {
			child.render(guiGraphics, mouseX, mouseY, delta);
		}
	}
	
}
