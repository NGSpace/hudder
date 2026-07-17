package dev.ngspace.ngsmcconfig.gui;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.blaze3d.platform.cursor.CursorTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * A custom-drawn dropdown control. It deliberately does not extend Minecraft's
 * Button, CycleButton, or EditBox classes so its appearance can be restyled
 * without inheriting vanilla button rendering.
 */
public class NGSMCConfigDropdownWidget<T> extends AbstractWidget implements NGSMCConfigOverlayWidget {

	protected static final int BORDER_COLOR = 0xFF9c9c9c;

	protected static final int TEXT_PADDING = 5;
	protected static final int ARROW_AREA_WIDTH = 18;

	protected final List<T> options;
	protected final Function<T, Component> valueText;
	protected final Consumer<T> selectionOperation;

	protected T value;
	protected boolean open;

	public NGSMCConfigDropdownWidget(int x, int y, int width, int height, List<T> options, T value,
			Function<T, Component> valueText, Consumer<T> selectionOperation) {
		super(x, y, width, height, Objects.requireNonNull(valueText).apply(value));
		this.options = List.copyOf(options);
		this.value = value;
		this.valueText = valueText;
		this.selectionOperation = Objects.requireNonNull(selectionOperation);
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
		setMessage(valueText.apply(value));
		closeOverlay();
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		if (!active)
			return;
		open = !open;
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		renderControl(graphics);
		
		if (this.isHovered()) {
			graphics.requestCursor(CursorTypes.POINTING_HAND);
		}
	}

	/**
	 * Main collapsed control. Override this method to replace the dropdown's base styling.
	 */
	protected void renderControl(GuiGraphicsExtractor graphics) {
		drawBox(graphics, getX(), getY(), getWidth(), getHeight(), 0xFF000000, open ? 0xFFFFFFFF : BORDER_COLOR);
		drawClippedText(graphics, getMessage(), getX() + TEXT_PADDING, getY(), getWidth() - ARROW_AREA_WIDTH - TEXT_PADDING);
		drawArrow(graphics, getRight() - ARROW_AREA_WIDTH / 2, getY() + getHeight() / 2, open, 0xFFFFFFFF);
	}

	@Override
	public void extractOverlayRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick,
			int overlayTop, int overlayBottom) {
		if (!open)
			return;

		int menuY = getMenuY(overlayTop, overlayBottom);
		for (int index = 0; index < options.size(); index++) {
			int optionY = menuY + index * getHeight();
			boolean hovered = contains(mouseX, mouseY, getX(), optionY, getWidth(), getHeight());
			T option = options.get(index);
			renderOption(graphics, option, getX(), optionY, hovered, Objects.equals(option, value));
		}
	}

	/**
	 * One expanded menu row. Override this method for deeper option-row styling.
	 */
	protected void renderOption(GuiGraphicsExtractor graphics, T option, int x, int y, boolean hovered,
			boolean selected) {
		
		if (hovered) graphics.requestCursor(CursorTypes.POINTING_HAND);
		
		int background = selected ? 0xFF46546B : 0xFF000000;
		// Originally thought about doing a whole "brighten" method thing but then realized that... nah.
		if (hovered) background = selected ? 0xFF66748B : 0xFF202020; 
		
		drawBox(graphics, x, y, getWidth(), getHeight(), background, 0xFFFFFFFF);
		drawClippedText(graphics, valueText.apply(option), x + TEXT_PADDING, y, getWidth() - TEXT_PADDING * 2);
	}

	@Override
	public boolean mouseClickedOverlay(MouseButtonEvent event, boolean doubleClick, int overlayTop, int overlayBottom) {
		if (!open || !active || !visible || !isValidClickButton(event.buttonInfo()))
			return false;

		double mouseX = event.x();
		double mouseY = event.y();

		if (contains(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
			playDownSound(Minecraft.getInstance().getSoundManager());
			onClick(event, doubleClick);
			return true;
		}

		int menuY = getMenuY(overlayTop, overlayBottom);
		if (contains(mouseX, mouseY, getX(), menuY, getWidth(), options.size() * getHeight())) {
			int selectedIndex = (int) ((mouseY - menuY) / getHeight());
			playDownSound(Minecraft.getInstance().getSoundManager());
			select(options.get(selectedIndex));
			return true;
		}

		closeOverlay();
		return false;
	}

	protected void select(T newValue) {
		if (!Objects.equals(value, newValue)) {
			setValue(newValue);
			selectionOperation.accept(newValue);
		} else {
			closeOverlay();
		}
	}

	protected int getMenuY(int overlayTop, int overlayBottom) {
		int menuHeight = options.size() * getHeight();
		int availableBelow = overlayBottom - getBottom();
		int availableAbove = getY() - overlayTop;
		return availableBelow >= menuHeight || availableBelow >= availableAbove ? getBottom() : getY() - menuHeight;
	}

	protected void drawBox(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int background,
			int border) {
		graphics.fill(x, y, x + width, y + height, background);
		graphics.fill(x, y, x + width, y + 1, border);
		graphics.fill(x, y + height - 1, x + width, y + height, border);
		graphics.fill(x, y, x + 1, y + height, border);
		graphics.fill(x + width - 1, y, x + width, y + height, border);
	}

	protected void drawClippedText(GuiGraphicsExtractor graphics, Component component, int x, int y, int width) {
		Font font = Minecraft.getInstance().font;
		int textY = y + (getHeight() - font.lineHeight) / 2;
		graphics.enableScissor(x, y + 1, x + Math.max(0, width), y + getHeight() - 1);
		graphics.text(font, component, x, textY, 0xFFFFFFFF, false);
		graphics.disableScissor();
	}
	
	// ChatGPT made this
	protected void drawArrow(GuiGraphicsExtractor graphics, int centerX, int centerY, boolean pointsUp, int color) {
		if (pointsUp) {
			graphics.fill(centerX - 3, centerY + 1, centerX + 4, centerY + 2, color);
			graphics.fill(centerX - 2, centerY, centerX + 3, centerY + 1, color);
			graphics.fill(centerX - 1, centerY - 1, centerX + 2, centerY, color);
		} else {
			graphics.fill(centerX - 3, centerY - 1, centerX + 4, centerY, color);
			graphics.fill(centerX - 2, centerY, centerX + 3, centerY + 1, color);
			graphics.fill(centerX - 1, centerY + 1, centerX + 2, centerY + 2, color);
		}
	}

	protected boolean contains(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	@Override
	public boolean isOverlayOpen() {
		return open;
	}

	@Override
	public void closeOverlay() {
		open = false;
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
