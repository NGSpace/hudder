package io.github.ngspace.hudder.main.config;

import java.net.URI;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.ngspace.ngsmcconfig.NGSMCConfigBuilder;
import dev.ngspace.ngsmcconfig.options.IntNGSMCConfigOption;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

public class HudderModMenuImpl implements ModMenuApi {
	@Override public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
		return new HudderConfigFactory();
    }
	
	class HudderConfigFactory implements ConfigScreenFactory<Screen> {

		@Override
		public Screen create(Screen parent) {
			var comp = Component.literal("Fuckin hell");
			comp.withStyle(t -> t.withHoverEvent(new HoverEvent.ShowText(Component.literal("r")))
					.withClickEvent(new ClickEvent.OpenUrl(URI.create("https://ngspace.github.io/"))));
			var builder = new NGSMCConfigBuilder(parent);
			builder.createCategory(Component.literal("1")).addOption(IntNGSMCConfigOption.builder(0, comp)
					.setDefaultValue(0).build());
			builder.createCategory(Component.literal("2")).addOption(IntNGSMCConfigOption.builder(0, comp)
					.setDefaultValue(0).build());
			builder.createCategory(Component.literal("3")).addOption(IntNGSMCConfigOption.builder(0, comp)
					.setDefaultValue(0).build());
			return builder.build();
		}
		
	}
}