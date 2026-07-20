package dev.ngspace.ngsmcconfig.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;

public class NGSMCConfigOptionsListWidget extends ContainerObjectSelectionList<NGSMCConfigEntry> {
    public static final int ROW_WIDTH = 290;

	public NGSMCConfigOptionsListWidget(Minecraft client, int width, int height, int x, int y) {
        super(client, width-x, height, y, 30);
        setPosition(x, y);
    }
    
    @Override
    public int addEntry(NGSMCConfigEntry entry) {
    	return super.addEntry(entry);
    }
    
    @Override protected void extractListBackground(GuiGraphicsExtractor guiGraphics) {/* It ugly ;_; */}
    
    @Override
    public int getRowWidth() {
    	return ROW_WIDTH;
    }

	public void extractOverlayRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		for (NGSMCConfigEntry entry : children())
			entry.extractOverlayRenderState(graphics, mouseX, mouseY, partialTick, getY(), getBottom());
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		for (NGSMCConfigEntry entry : children()) {
			if (entry.mouseClickedOverlay(event, doubleClick, getY(), getBottom()))
				return true;
		}

		boolean handled = super.mouseClicked(event, doubleClick);
		if (handled)
			closeAllButFirstOpenOverlay();
		return handled;
	}

	private void closeAllButFirstOpenOverlay() {
		NGSMCConfigEntry openEntry = null;
		for (NGSMCConfigEntry entry : children()) {
			if (!entry.isOverlayOpen())
				continue;
			if (openEntry == null)
				openEntry = entry;
			else
				entry.closeOverlay();
		}
	}
}
