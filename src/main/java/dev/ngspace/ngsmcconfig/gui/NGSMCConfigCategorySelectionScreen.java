package dev.ngspace.ngsmcconfig.gui;

import java.util.List;

import dev.ngspace.ngsmcconfig.NGSMCConfigCategory;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public class NGSMCConfigCategorySelectionScreen extends AbstractNGSMCConfigScreen {
	
	public static final int CATEGORY_PADDING = 8;

	public NGSMCConfigCategorySelectionScreen(Screen parentScreen,
			List<NGSMCConfigCategory> categories) {
		super(parentScreen, categories, false);
	}
	
	@Override
	protected void init() {
		super.init();
		
		final int increase = CATEGORY_PADDING + 20;
		
		int catY = height/2-increase*(categories.size()/2);
		
		for (int i = 0;i<categories.size();i++) {
			var category = categories.get(i);
			
			int catX = width/2;
			if (i%2==0) {
				catY+= increase;
				catX=catX-150-CATEGORY_PADDING/2;
			} else catX=catX+CATEGORY_PADDING/2;
			
			addRenderableWidget(Button.builder(category.title(),
				b->minecraft.setScreen(new NGSMCConfigOptionsScreen(this, categories, category)))
				.pos(catX, catY)
				.build());
		}
	}
	
}