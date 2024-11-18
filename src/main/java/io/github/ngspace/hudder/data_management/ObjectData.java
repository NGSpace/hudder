package io.github.ngspace.hudder.data_management;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ObjectData {private ObjectData() {}
	
	@SuppressWarnings("unused")
	public static Object getObject(String key) {
		return switch (key) {
			
			case "componenttypes": yield new Object() {
				public Object get(String componentName) {
//					System.out.println(Registries.DATA_COMPONENT_TYPE.getIds().getClass());
					return Registries.DATA_COMPONENT_TYPE.get(Identifier.of(componentName));
				}
			};
			
			default: yield null;
		};
	}
}
