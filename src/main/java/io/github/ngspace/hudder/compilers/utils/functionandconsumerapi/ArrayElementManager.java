package io.github.ngspace.hudder.compilers.utils.functionandconsumerapi;

import java.util.ArrayList;

import io.github.ngspace.hudder.uielements.AUIElement;

public class ArrayElementManager extends ArrayList<AUIElement> implements IUIElementManager {

	private static final long serialVersionUID = -504951216427576657L;

	@Override public void addUIElement(AUIElement UIElement) {add(UIElement);}
	@Override public AUIElement[] toUIElementArray() {return toArray(new AUIElement[size()]);}
}
