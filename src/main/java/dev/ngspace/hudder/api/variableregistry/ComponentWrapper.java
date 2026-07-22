package dev.ngspace.hudder.api.variableregistry;

import dev.ngspace.hudder.utils.ValueGetter;
import net.minecraft.network.chat.Component;

/**
 * Wraps a Minecraft {@link Component} so that HUDs can access selected
 * component properties through the {@link ValueGetter} interface without
 * receiving the component directly.
 * <p>
 * The wrapped component remains publicly accessible to Java code through
 * {@link #component}.
 * </p>
 */
public class ComponentWrapper implements ValueGetter {

	/**
	 * The wrapped Minecraft component.
	 */
	public Component component;
	
	/**
	 * Creates a new wrapper for the specified component.
	 *
	 * @param component the component to wrap
	 */
	public ComponentWrapper(Component component) {
		this.component = component;
	}
	
	/**
	 * Returns a selected property of the wrapped component.
	 * <p>
	 * Supported keys are:
	 * {@code string}, {@code color}, {@code shadow_color}, {@code bold},
	 * {@code italic}, {@code obfuscated}, {@code strikethrough},
	 * {@code underlined}, and {@code siblings}. Sibling components are returned
	 * as a list of {@link ComponentWrapper} instances.
	 * </p>
	 *
	 * @param key the name of the component property to retrieve
	 * @return the value associated with the specified key, or {@code null} if
	 *         the key is not supported
	 */
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
