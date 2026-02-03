package dev.ngspace.hudder.data_management;

import dev.ngspace.hudder.data_management.api.DataVariable;
import net.minecraft.client.Minecraft;

public class ResourcePackVariables implements DataVariable<Object> {
	@Override
	public Object getValue0(String key) {
		var ins = Minecraft.getInstance();
		return switch (key) {
			case "selectedresourcepacks": yield ins.getResourcePackRepository().getSelectedPacks().stream()
					.filter(pack->!pack.isRequired())
					.map(t -> t.getTitle().getString())
					.toList();
			case "selectedresourcepacks_unfiltered": yield ins.getResourcePackRepository().getSelectedPacks().stream()
					.map(t -> t.getTitle().getString())
					.toList();
			default: yield null;
		};
	}
}
