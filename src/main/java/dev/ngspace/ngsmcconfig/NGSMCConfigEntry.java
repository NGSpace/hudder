package dev.ngspace.ngsmcconfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.ngspace.hudder.Hudder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

public class NGSMCConfigEntry extends ContainerObjectSelectionList.Entry<NGSMCConfigEntry>{
	
    AbstractWidget widget;
    Button resetButton;
    StringWidget text;
    
    List<AbstractWidget> children;
    
    protected NGSMCConfigEntry() {}

    public NGSMCConfigEntry(AbstractWidget widget, Component title) {
        this.widget = widget;
        resetButton = Button.builder(Component.literal("Reset"), null).size(40, 20).build();
        text = new StringWidget(0, 0, 200, 20, title, Minecraft.getInstance().font);
        children = Arrays.asList(resetButton, widget, text);
    }

    @Override
    public void render(GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY,
    		boolean hovered, float partialTick) {
    	resetButton.setPosition(x+width-40, y);
        resetButton.render(graphics, mouseX, mouseY, partialTick);
        if (widget!=null) {
        	widget.setPosition(x+width-50-widget.getWidth(), y);
        	widget.render(graphics, mouseX, mouseY, partialTick);
        }
        text.setPosition(0, y);
        text.render(graphics, mouseX, mouseY, partialTick);
        var c = getChildAt(mouseX, mouseY);
        if (c.isPresent()) {
        	var child = c.get();
        	if (child==text) {
        		text.renderWidget(graphics, height, mouseY, partialTick);
        	}
        }
    }

    @Override public List<? extends GuiEventListener> children() {return children;}

	@Override public List<? extends NarratableEntry> narratables() {return children;}
}
