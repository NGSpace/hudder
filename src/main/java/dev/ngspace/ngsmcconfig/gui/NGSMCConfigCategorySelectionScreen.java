package dev.ngspace.ngsmcconfig.gui;

import java.io.File;
import java.net.URI;
import java.util.List;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public class NGSMCConfigCategorySelectionScreen extends AbstractNGSMCConfigScreen {
	
	public static final int CATEGORY_PADDING = 8;

	public NGSMCConfigCategorySelectionScreen(Screen parentScreen,
			List<NGSMCConfigCategory> categories, Runnable writeoperation, URI wikiUri, File configfile) {
		super(parentScreen, categories, false, writeoperation, wikiUri, configfile);
		askBeforeUnsavedLeave = true;
	}
	
	@Override
	protected void init() {
		super.init();
		
		final int increase = CATEGORY_PADDING + 20;
		
		int catY = (int) (height/2d-increase*(Math.ceil(categories.size()/2d)));
		
		for (int i = 0;i<categories.size();i++) {
			var category = categories.get(i);
			
			int catX = width/2;
			if (i%2==0) {
				catY+= increase;
				catX=catX-150-CATEGORY_PADDING/2;
				if (i+1==categories.size()) {
					catX+=(150+CATEGORY_PADDING/2)/2;
				}
			} else catX=catX+CATEGORY_PADDING/2;
			
			addRenderableWidget(Button.builder(category.title(),
				b->minecraft.setScreen(new NGSMCConfigOptionsScreen(this, categories, category, writeoperation, wikiUri, configfile)))
				.pos(catX, catY)
				.build());
		}
	}
	
}