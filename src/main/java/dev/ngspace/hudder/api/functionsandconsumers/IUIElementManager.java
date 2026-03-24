package dev.ngspace.hudder.api.functionsandconsumers;

import dev.ngspace.hudder.uielements.AUIElement;

public interface IUIElementManager {
	public void addUIElement(AUIElement UIElement);

	public AUIElement[] toUIElementArray();
}
