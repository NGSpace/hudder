package dev.ngspace.ngsmcconfig.gui;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.platform.cursor.CursorTypes;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

public class NGSMCCategoryList extends ContainerObjectSelectionList<NGSMCCategoryList.Entry> {

	public NGSMCCategoryList(Minecraft minecraft, int width, int height, int y) {
		super(minecraft, width, height, y, 20);
	}
	
	public void addCategory(AbstractNGSMCConfigScreen screen, NGSMCConfigCategory category, boolean isSelected) {
		addEntry(new Entry(screen, category, isSelected));
	}
	
	@Override
	public int getRowWidth() {
		return getWidth();
	}
	
	@Override
	protected void extractListBackground(GuiGraphicsExtractor graphics) {
		// TODO Auto-generated method stub
		super.extractListBackground(graphics);
	}
	
	public static class Entry extends ContainerObjectSelectionList.Entry<NGSMCCategoryList.Entry> {

		private AbstractNGSMCConfigScreen screen;
		private NGSMCConfigCategory category;
		private boolean selected;
		private NGSMCConfigIcon icon;
		private final Button categoryButton;
		private final long marqueeStartTime;
		
		public Entry(AbstractNGSMCConfigScreen screen, NGSMCConfigCategory category, boolean isSelected) {
			this.screen = screen;
			this.category = category;
			this.selected = isSelected;
			this.icon = category.icon();
			this.marqueeStartTime = System.currentTimeMillis();
			this.categoryButton = Button.builder(category.title(), _->openCategory())
					.bounds(0, 0, 1, 1)
					.build();
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return Collections.singletonList(categoryButton);
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return Collections.singletonList(categoryButton);
		}
		
		@Override
		public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
	    	int x = getX();
	    	int width = getWidth();
	    	int height = getHeight();
	    	int y = getY();
	    	
	    	categoryButton.setPosition(x, y);
	    	categoryButton.setSize(width, height);

	    	if (selected) {
	    		graphics.fill(x, y, x+width, y+height, 0x11FFFFFF);
	    		graphics.fill(x, y, x+2, y+height, 0xFFFFFFFF);
	    	}
	    	
	    	if (hovered) {
	    		graphics.fill(x, y, x+width, y+height, 0x30FFFFFF);
				graphics.requestCursor(CursorTypes.POINTING_HAND);
	    	}
	    	
	    	renderCategoryTitle(graphics, x, y, width, height, selected ? 0xFFFFFFFF : 0xFF9C9C9C);
	    	if (icon!=null)
	    		icon.extractRenderState(graphics, mouseX, mouseY, a, height-4, height-4, x+2, y+2);
	    	
	    	if (categoryButton.isFocused()) {
	    		graphics.fill(x+width-2, y, x+width, y+height, 0xFF00FFFF);
	    	}
		}
		
		private void renderCategoryTitle(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
			int textX = x + NGSMCScrollingText.ICON_TEXT_X_OFFSET;
			int textRight = x + width - NGSMCScrollingText.TEXT_RIGHT_PADDING;

			NGSMCScrollingText.render(graphics, category.title(), textX, y, textRight, height, color,
					marqueeStartTime);
		}

		private void openCategory() {
			Minecraft.getInstance().gui.setScreen(new NGSMCConfigOptionsScreen(screen, category, screen.root));
		}
		
	}
}
