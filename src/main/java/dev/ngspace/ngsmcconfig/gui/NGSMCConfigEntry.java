package dev.ngspace.ngsmcconfig.gui;

import java.util.Arrays;
import java.util.List;

import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class NGSMCConfigEntry extends ContainerObjectSelectionList.Entry<NGSMCConfigEntry>{
	
    AbstractWidget widget;
    Button resetButton;
    StringWidget text;
    
    List<AbstractWidget> children;
	private AbstractNGSMCConfigOption<?> option;
    
    protected NGSMCConfigEntry() {}

    public NGSMCConfigEntry(AbstractWidget widget, Component title, AbstractNGSMCConfigOption<?> option) {
        this.widget = widget;
        resetButton = Button.builder(Component.translatable("ngsmcconfig.reset"), button->option.reset())
        		.size(40, 20).build();
        text = new StringWidget(0, 0, 200, 20, title, Minecraft.getInstance().font) {
        	@Override public void playDownSound(SoundManager soundManager) { /* Ugly noise */ }
        };
//        text.alignLeft();
        text.active = true;
        this.option = option;
        children = Arrays.asList(widget, resetButton, text);
    }

    @Override
    public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
    	int x = getX();
    	int width = getWidth();
    	int height = getHeight();
    	int y = getY();
    	resetButton.setPosition(x+width-40, y);
        resetButton.render(graphics, mouseX, mouseY, partialTick);
        resetButton.active = !option.isDefault();
        if (widget!=null) {
        	widget.setPosition(x+width-50-widget.getWidth(), y);
        	widget.render(graphics, mouseX, mouseY, partialTick);
        }
        text.setPosition(x, y);
        text.render(graphics, mouseX, mouseY, partialTick);
        var c = getChildAt(mouseX, mouseY);
        if (c.isPresent()) {
        	var child = c.get();
        	if (child==text) {
        		text.renderWidget(graphics, height, mouseY, partialTick);
        	}
        }
        
    	var hoveredwidget = this.getChildAt(mouseX, mouseY);
        
        if (hoveredwidget.isPresent() && hoveredwidget.get() instanceof AbstractWidget hoveredawidget) {
            Style style = hoveredawidget.getMessage().getStyle();
            graphics.renderComponentHoverEffect(Minecraft.getInstance().font, style, mouseX-5, mouseY+10);
        }
    }

    @Override public List<? extends GuiEventListener> children() {return children;}

	@Override public List<? extends NarratableEntry> narratables() {return children;}
}
