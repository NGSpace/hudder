package dev.ngspace.ngsmcconfig.gui;

import dev.ngspace.ngsmcconfig.NGSMCConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public class ContainerWidget extends ContainerObjectSelectionList<NGSMCConfigEntry> {
    public ContainerWidget(Minecraft client, int width, int height, int y) {
        super(client, width, height, y, 30, 15);
    }
    
    @Override
    public int addEntry(NGSMCConfigEntry entry) {
    	return super.addEntry(entry);
    }
    
//    @Override protected void renderListBackground(GuiGraphics guiGraphics) {/* It ugly ;_; */}
    
    @Override
    public int getRowWidth() {
    	return width-33;
    }
}
