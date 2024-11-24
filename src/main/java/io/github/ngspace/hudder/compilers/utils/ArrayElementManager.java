package io.github.ngspace.hudder.compilers.utils;

import java.util.ArrayList;

import io.github.ngspace.hudder.methods.elements.AUIElement;

public class ArrayElementManager extends ArrayList<AUIElement> implements IElementManager {

	private static final long serialVersionUID = -504951216427576657L;

	@Override public void addElem(AUIElement UIElement) {add(UIElement);}
	@Override public AUIElement[] toElementArray() {return toArray(new AUIElement[size()]);}
}
