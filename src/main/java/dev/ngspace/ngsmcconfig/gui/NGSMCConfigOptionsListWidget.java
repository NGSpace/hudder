package dev.ngspace.ngsmcconfig.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public class NGSMCConfigOptionsListWidget extends ContainerObjectSelectionList<NGSMCConfigEntry> {
    public NGSMCConfigOptionsListWidget(Minecraft client, int width, int height, int y) {
        super(client, width, height, y, 30);
    }
    
    @Override
    public int addEntry(NGSMCConfigEntry entry) {
    	return super.addEntry(entry);
    }
    
    @Override protected void renderListBackground(GuiGraphics guiGraphics) {/* It ugly ;_; */}
    @Override protected void renderListSeparators(GuiGraphics guiGraphics) {/* It ugly too */}
    
    @Override
    public int getRowWidth() {
    	return 300;
    }
}
