package io.github.ngspace.hudder.data_management;

import java.util.function.Function;

import net.minecraft.client.Minecraft;

public class ResourcePackVariables implements Function<String, Object> {
	@Override
	public Object apply(String key) {
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
