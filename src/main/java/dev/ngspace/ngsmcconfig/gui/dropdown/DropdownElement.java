package dev.ngspace.ngsmcconfig.gui.dropdown;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DropdownElement extends AbstractWidget {

	public boolean shown;

	public DropdownElement(int i, int j, int k, int l, Component component) {
		super(i, j, k, l, component);
		setTabOrderGroup(100);
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
		guiGraphics.fill(getX(), getY(), getRight(), getBottom(), 0xFFFFFFFF);
		ResourceLocation resourceLocation = ResourceLocation.withDefaultNamespace("widget/text_field");
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, resourceLocation, this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
		shown = !shown;
		setHeight(20+(shown?100:0));
		return true;
	}
}
