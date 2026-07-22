package dev.ngspace.hudder.api.functionsandconsumers;

import dev.ngspace.hudder.uielements.AUIElement;

/**
 * Manages a collection of UI elements created during HUD compilation or
 * execution.
 */
public interface IUIElementManager {
	/**
	 * Adds a UI element to this manager.
	 *
	 * @param UIElement the UI element to add
	 */
	public void addUIElement(AUIElement UIElement);

	/**
	 * Returns the UI elements currently held by this manager as an array.
	 *
	 * @return an array containing the managed UI elements
	 */
	public AUIElement[] toUIElementArray();
}
