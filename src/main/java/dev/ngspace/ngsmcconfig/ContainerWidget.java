package dev.ngspace.ngsmcconfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public class ContainerWidget extends ContainerObjectSelectionList<NGSMCConfigEntry> {
	public ContainerWidget(Minecraft minecraft, int width, int height, int y, int itemHeight) {
		super(minecraft, width, height, y, itemHeight);
	}
	
	@Override
	public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
		// TODO Auto-generated method stub
		super.renderWidget(guiGraphics, i, j, f);
//		guiGraphics.fill(getX(), getY(), getRight(), getBottom(), 0xffFFffFF);
	}
	
	@Override
	public int addEntry(NGSMCConfigEntry entry) {
		return super.addEntry(entry);
	}
}
