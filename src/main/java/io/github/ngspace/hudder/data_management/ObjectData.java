package io.github.ngspace.hudder.data_management;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ObjectData {private ObjectData() {}
	
	@SuppressWarnings("unused")
	public static Object getObject(String key) {
		return switch (key) {
			
			case "componenttypes": yield new Object() {
				public Object get(String componentName) {
					return Registries.DATA_COMPONENT_TYPE.get(Identifier.of(componentName));
				}
			};
			
			default: yield null;
		};
	}
}
