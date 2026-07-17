package dev.ngspace.ngsmcconfig.gui;

import java.util.Collections;
import java.util.List;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;

public class NGSMCCategoryList extends ContainerObjectSelectionList<NGSMCCategoryList.Entry> {

	public NGSMCCategoryList(Minecraft minecraft, int width, int height, int y) {
		super(minecraft, width, height, y, 20);
	}
	
	public void addCategory(AbstractNGSMCConfigScreen screen, NGSMCConfigCategory category, boolean isSelected) {
		addEntry(new Entry(screen, this, category, isSelected));
	}
	
	@Override
	public int getRowWidth() {
		return getWidth();
	}
	
	public static class Entry extends ContainerObjectSelectionList.Entry<NGSMCCategoryList.Entry> {

		private AbstractNGSMCConfigScreen screen;
		private NGSMCConfigCategory category;
		private boolean selected;
		private NGSMCConfigIcon icon;
		private NGSMCCategoryList holdinglist;
		
		public Entry(AbstractNGSMCConfigScreen screen, NGSMCCategoryList holdinglist, NGSMCConfigCategory category, boolean isSelected) {
			this.screen = screen;
			this.category = category;
			this.selected = isSelected;
			this.holdinglist = holdinglist;
			this.icon = category.icon();
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return Collections.emptyList();
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return Collections.emptyList();
		}
		
		@Override
		public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
	    	int x = getX();
	    	int width = getWidth();
	    	int height = getHeight();
	    	int y = getY();
	    	
	    	graphics.text(Minecraft.getInstance().font, category.title(), x+20, y+7, 0xFFFFFFFF);
	    	icon.extractRenderState(graphics, mouseX, mouseY, a, height-4, height-4, x+2, y+2);
	    	
	    	if (isFocused()) {
	    		graphics.fill(width-6, y, width-6, y+height, 0xFFFFFFFF);
	    	}
		}
		
		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			Minecraft.getInstance().gui.setScreen(new NGSMCConfigOptionsScreen(screen, category, screen.root));
			return super.mouseClicked(event, doubleClick);
		}
		
	}
}
