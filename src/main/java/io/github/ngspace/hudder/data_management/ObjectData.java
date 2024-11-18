package io.github.ngspace.hudder.data_management;

import io.github.ngspace.hudder.util.ValueGetter;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ObjectData {private ObjectData() {}
	
	public static Object getObject(String key) {
		return switch (key) {
			
			case "componenttypes": yield (ValueGetter) name->Registries.DATA_COMPONENT_TYPE.get(Identifier.of(name));
			
			default: yield null;
		};
	}
}
