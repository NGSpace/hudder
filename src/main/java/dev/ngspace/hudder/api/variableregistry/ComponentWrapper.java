package dev.ngspace.hudder.api.variableregistry;

import dev.ngspace.hudder.utils.ValueGetter;
import net.minecraft.network.chat.Component;

public class ComponentWrapper implements ValueGetter {

	public Component component;

	public ComponentWrapper(Component component) {
		this.component = component;
	}

	@Override
	public Object get(String key) {
		return switch (key) {
			case "string": yield component.getString();
			case "color": yield component.getStyle().getColor();
			case "shadow_color": yield component.getStyle().getShadowColor();
			case "bold": yield component.getStyle().isBold();
			case "italic": yield component.getStyle().isItalic();
			case "obfuscated": yield component.getStyle().isObfuscated();
			case "strikethrough": yield component.getStyle().isStrikethrough();
			case "underlined": yield component.getStyle().isUnderlined();
			case "siblings": yield component.getSiblings().stream().map(ComponentWrapper::new).toList();
			default: yield null;
		};
	}

	@Override
	public String toString() {
		return component.getString();
	}
}
