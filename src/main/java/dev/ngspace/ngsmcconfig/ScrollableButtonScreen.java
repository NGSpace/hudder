package dev.ngspace.ngsmcconfig;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * A scrollable screen containing a list of buttons using Mojang mappings.
 */
public class ScrollableButtonScreen extends Screen {
    private ButtonListWidget buttonList;

    public ScrollableButtonScreen() {
        super(Component.literal("Scrollable Buttons"));
    }

    @Override
    protected void init() {
        super.init();
        Minecraft mc = Minecraft.getInstance();
        // Define scrollable area: top 32px, bottom height-32px, each item 25px tall
        this.buttonList = new ButtonListWidget(mc, this.width, this.height, 32, this.height - 32, 25);

        // Populate list with buttons
        for (int i = 0; i < 50; i++) {
            final int idx = i;
            Button btn = Button.builder(Component.literal("Button " + i), b->{}).bounds(0, 0, 100, 20).build();
            this.buttonList.addEntry(new ButtonEntry(btn));
        }

        // Add list to screen and focus
        this.addWidget(this.buttonList);
        this.setFocused(this.buttonList);

        // Add Done button
//        this.addRenderableWidget(new Button(
//            this.width / 2 - 100, this.height - 27, 200, 20,
//            new TextComponent("Done"), button -> mc.setScreen(null)
//        ));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
//        this.renderBackground(graphics);
        this.buttonList.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Entry wrapping a single Button.
     */
    private class ButtonEntry extends ContainerObjectSelectionList.Entry<ButtonEntry> {
        private final Button button;

        ButtonEntry(Button button) {
            this.button = button;
        }

        @Override
        public void render(GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
            button.setPosition(x, y);
            this.button.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.singletonList(this.button);
        }

		@Override
		public List<? extends NarratableEntry> narratables() {
			return Collections.emptyList();
		}
    }

    /**
     * Scrollable list widget for ButtonEntry.
     */
    private static class ButtonListWidget extends ContainerObjectSelectionList<ButtonEntry> {
        public ButtonListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
        }
        
        @Override
        public int addEntry(ButtonEntry entry) {
        	return super.addEntry(entry);
        }
    }
}
