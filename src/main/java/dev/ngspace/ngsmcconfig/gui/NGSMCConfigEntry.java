package dev.ngspace.ngsmcconfig.gui;

import java.util.Arrays;
import java.util.List;

import dev.ngspace.hudder.mixin.GuiGraphicsExtractorAccessor;
import dev.ngspace.ngsmcconfig.options.AbstractNGSMCConfigOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.StringWidget.TextOverflow;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class NGSMCConfigEntry extends ContainerObjectSelectionList.Entry<NGSMCConfigEntry> {
	
	public boolean renderlast;
	
    AbstractWidget widget;
    Button resetButton;
    StringWidget text;
    
    List<AbstractWidget> children;
	private AbstractNGSMCConfigOption<?> option;
    
    protected NGSMCConfigEntry() {}
    
    public NGSMCConfigEntry(AbstractWidget widget, Component title, AbstractNGSMCConfigOption<?> option) {
    	this(widget,title,option,false);
    }

    public NGSMCConfigEntry(AbstractWidget widget, Component title, AbstractNGSMCConfigOption<?> option,
    		boolean renderlast) {
    	this.renderlast = renderlast;
        this.widget = widget;
        this.option = option;
        resetButton = Button.builder(Component.translatable("ngsmcconfig.reset"), _->option.reset())
        		.size(40, 20).build();
        text = new StringWidget(0, 0, 210, 20, title, Minecraft.getInstance().font) {
        	@Override public void playDownSound(SoundManager soundManager) { /* Ugly noise */ }
        };
        text.active = true;
        text.setMaxWidth(text.getWidth(), TextOverflow.SCROLLING);
        children = widget==null ? Arrays.asList(text) : Arrays.asList(widget, resetButton, text);
    }

    @Override
    public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
    	int x = getX();
    	int width = getWidth();
    	int height = getHeight();
    	int y = getY();
    	
        if (widget!=null) {
        	resetButton.setPosition(x+width-40, y);
            resetButton.active = !option.isDefault();
            resetButton.extractRenderState(graphics, mouseX, mouseY, partialTick);
            
        	widget.setPosition(x+width-50-widget.getWidth(), y);
        	widget.extractRenderState(graphics, mouseX, mouseY, partialTick);
        } else {
        	text.setSize(width, height);
        }
        text.setPosition(x, y);
        text.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
        var c = getChildAt(mouseX, mouseY);
        if (c.isPresent()) {
        	var child = c.get();
        	if (child==text) {
        		text.extractWidgetRenderState(graphics, height, mouseY, partialTick);
        	}
        }
        
    	var hoveredwidget = this.getChildAt(mouseX, mouseY);
        
        if (hoveredwidget.isPresent() && hoveredwidget.get() instanceof AbstractWidget hoveredawidget) {
            Style style = hoveredawidget.getMessage().getStyle();
            ((GuiGraphicsExtractorAccessor) graphics).callComponentHoverEffect(Minecraft.getInstance().font, style, mouseX-5, mouseY+10);
        }
    }

    @Override public List<? extends GuiEventListener> children() {return children;}

	@Override public List<? extends NarratableEntry> narratables() {return children;}

	public void extractOverlayRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick,
			int overlayTop, int overlayBottom) {
		if (renderlast && widget instanceof NGSMCConfigOverlayWidget overlayWidget)
			overlayWidget.extractOverlayRenderState(graphics, mouseX, mouseY, partialTick, overlayTop, overlayBottom);
	}

	public boolean mouseClickedOverlay(MouseButtonEvent event, boolean doubleClick, int overlayTop, int overlayBottom) {
		return renderlast && widget instanceof NGSMCConfigOverlayWidget overlayWidget
				&& overlayWidget.mouseClickedOverlay(event, doubleClick, overlayTop, overlayBottom);
	}

	public boolean isOverlayOpen() {
		return renderlast && widget instanceof NGSMCConfigOverlayWidget overlayWidget && overlayWidget.isOverlayOpen();
	}

	public void closeOverlay() {
		if (widget instanceof NGSMCConfigOverlayWidget overlayWidget)
			overlayWidget.closeOverlay();
	}
}
