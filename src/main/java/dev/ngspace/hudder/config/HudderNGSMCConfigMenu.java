package dev.ngspace.hudder.config;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.compilers.CompilerRegistry;
import dev.ngspace.hudder.api.compilers.interfaces.SettingsProvider;
import dev.ngspace.hudder.api.compilers.utils.CompilerEntry;
import dev.ngspace.hudder.main.HudderTickEvent;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigBuilder;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigIcon;
import dev.ngspace.ngsmcconfig.options.BooleanNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.DoubleNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.DropdownNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.HexNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.IntNGSMCConfigOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HudderNGSMCConfigMenu { private HudderNGSMCConfigMenu() {}
	
	protected static Minecraft mc = Minecraft.getInstance();

	public static Screen createMenu(Screen parent) {
		// I hate this. I hate this. I hate this. I hate this. I hate this. I hate this. I hate this.
		HudderUserSettings config = Hudder.config.userSettings;
		CompilerRegistry registry = Hudder.config.registry;
		
		Function<Boolean, Component> enabledDisabled = v -> Boolean.TRUE.equals(v) ? Component.translatable(
				"hudder.ngsmcconfig.enabled") : Component.translatable("hudder.ngsmcconfig.disabled");
		
		var builder = new NGSMCConfigBuilder(parent);
		builder.setWriteOperation(() -> {
			try {
				Hudder.config.save();
				HudFileUtils.reloadResources();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		builder.setDocsUri(URI.create("https://ngspace.dev/hudder"));
		builder.setConfigFile(HudFileUtils.FOLDER.toFile());
		builder.setConfigButtonText(Component.translatable("hudder.ngsmcconfig.config"));
		
		
		
		// Huds
		// NGSMCConfig changes the size and position anyways
		var widget = new HudSelectionList(Minecraft.getInstance(), HudFileUtils.FOLDER, config, registry);
		builder.addCustomWidgetCategory(Component.translatable("hudder.mainfile"),
				new NGSMCConfigIcon.SpriteIcon("items", "item/map"),
				widget, widget::save, widget::reset, widget::error, widget::warning);
		builder.setDragAndDropConsumer(files->{
			for (Path p : files) {
				try {
					HudderTickEvent.TEMP_DISABLE = true;
					Path dest = HudFileUtils.FOLDER.resolve(p.getFileName());
					
					if (Files.exists(dest)) {
						throw new IOException("Hud already exists");
					}
					
					if (Files.isDirectory(p)) {
						FileUtils.copyDirectory(p.toFile(), dest.toFile());
					} else {
						Files.copy(p, dest);
					}
				} catch (IOException e) {
					Hudder.showWarningToast(Component.literal("Failed to copy hud"),
							Component.literal("Failed to copy hud file"));
					if (Hudder.IS_DEBUG) e.printStackTrace();
				} finally {
					HudderTickEvent.TEMP_DISABLE = false;
				}
			}
		});
		
		
		
		// General
		NGSMCConfigCategory general = builder.createCategory(Component.translatable("hudder.general"),
				new NGSMCConfigIcon.SpriteIcon("items", "item/compass_00"));
		NGSMCConfigCategory text_rendering = builder.createCategory(Component.translatable("hudder.text_rendering"),
				new NGSMCConfigIcon.SpriteIcon("items", "item/name_tag"));
		NGSMCConfigCategory text_padding = builder.createCategory(Component.translatable("hudder.text_padding"),
				new NGSMCConfigIcon.SpriteIcon("items", "item/structure_void"));
		NGSMCConfigCategory vanillahud = builder.createCategory(Component.translatable("hudder.vanillahud"),
				new NGSMCConfigIcon.SpriteIcon("gui", "hud/heart/full"));
		NGSMCConfigCategory safety_perf = builder.createCategory(Component.translatable("hudder.safety_perf"),
				new NGSMCConfigIcon.SpriteIcon("gui", "mob_effect/resistance"));
		
		
		
		NGSMCConfigCategory hudsettings = builder.createCategory(Component.translatable("hudder.hudsettings",
				config.mainfile),
				new NGSMCConfigIcon.SpriteIcon("items", "item/amethyst_shard"));
		
		/* General */
		general.addOption(BooleanNGSMCConfigOption.builder(config.enabled, Component.translatable("hudder.general.enabled"))
				.setHoverComponent(Component.translatable("hudder.general.enabled.tooltip"))
				.setDefaultValue(true)
				.setSaveOperation(b->config.enabled=b)
				.setComponentProvider(enabledDisabled)
				.build());
		general.addOption(DropdownNGSMCConfigOption.builder(registry.findEntryFromId(Hudder.config.compilerId())
				.orElseThrow(()->new IllegalArgumentException("No compiler named "
						+ Hudder.config.compilerId())).display_name(),
					Component.translatable("hudder.general.compilertype"),
					registry.entries().stream()
						.sorted(Comparator.comparing(CompilerEntry::unstable)
								.thenComparing(CompilerEntry::deprecated)
								.thenComparing(CompilerEntry::display_name, String.CASE_INSENSITIVE_ORDER))
						.map(e->e.display_name())
						.toList())
	    		.setHoverComponent(Component.translatable("hudder.general.compilertype.tooltip"))
	    		.setDefaultValue("Hudder V3")
	    		// Should be safe since the compiler has either been selected from the list which means it's
	    		// safe. Or the first check during the builder's initation would've thrown already.
	    		.setSaveOperation(b->Hudder.config.setCompilerName(registry.findEntryFromDisplayName(b).get()
	    				.id()))
	    		.setValidator(e->{
	    			widget.comp = e;
	    			return registry.findEntryFromDisplayName(e).isPresent()
	    					? null : Component.translatable("hudder.general.compilertype.error");
	    		})
	    		.setWarningProvider(e->getCompilerWarning(registry.findEntryFromDisplayName(e).get()))
	    		.build());
		general.addOption(DoubleNGSMCConfigOption.builder(config.scale, Component.translatable("hudder.general.scale"))
				.setHoverComponent(Component.translatable("hudder.general.scale.tooltip"))
				.setSaveOperation(b->config.scale=b.floatValue())
				.setDefaultValue(1d)
				.build());
		
		
		
		/* Text */
		text_rendering.addOption(HexNGSMCConfigOption.builder(config.color, Component.translatable("hudder.text_rendering.color"))
				.setHoverComponent(Component.translatable("hudder.text_rendering.color.tooltip"))
				.setDefaultValue(0xFFd6d6d6)
				.setSaveOperation(b->config.color=b)
				.build());
		text_rendering.addOption(BooleanNGSMCConfigOption.builder(config.shadow, Component.translatable("hudder.text_rendering.shadow"))
				.setHoverComponent(Component.translatable("hudder.text_rendering.shadow.tooltip"))
				.setSaveOperation(b->config.shadow=b)
				.setDefaultValue(true)
				.setComponentProvider(enabledDisabled)
				.build());
		text_rendering.addOption(BooleanNGSMCConfigOption.builder(config.background, Component.translatable("hudder.text_rendering.background"))
				.setHoverComponent(Component.translatable("hudder.text_rendering.background.tooltip"))
				.setSaveOperation(b->config.background=b)
				.setDefaultValue(true)
				.setComponentProvider(enabledDisabled)
				.build());
		text_rendering.addOption(HexNGSMCConfigOption.builder(config.backgroundcolor,
				Component.translatable("hudder.text_rendering.backgroundcolor"))
				.setHoverComponent(Component.translatable("hudder.text_rendering.backgroundcolor.tooltip"))
				.setDefaultValue(0x86353535)
				.setSaveOperation(b->config.backgroundcolor=b)
				.build());
		text_rendering.addOption(IntNGSMCConfigOption.builder(config.lineHeight, Component.translatable("hudder.text_rendering.height"))
				.setHoverComponent(Component.translatable("hudder.text_rendering.height.tooltip"))
				.setSaveOperation(b->config.lineHeight=b)
				.setDefaultValue(10)
				.build());
		
		
		/* Text Padding */
		text_padding.addOption(IntNGSMCConfigOption.builder(config.yoffset_top, Component.translatable("hudder.text_padding.yoffset_top"))
				.setHoverComponent(Component.translatable("hudder.text_padding.yoffset_top.tooltip"))
				.setSaveOperation(b->config.yoffset_top=b)
				.setDefaultValue(1)
				.build());
		text_padding.addOption(IntNGSMCConfigOption.builder(config.yoffset_bottom, Component.translatable("hudder.text_padding.yoffset_bottom"))
				.setHoverComponent(Component.translatable("hudder.text_padding.yoffset_bottom.tooltip"))
				.setSaveOperation(b->config.yoffset_bottom=b)
				.setDefaultValue(0)
				.build());
		text_padding.addOption(IntNGSMCConfigOption.builder(config.xoffset_left, Component.translatable("hudder.text_padding.xoffset_left"))
				.setHoverComponent(Component.translatable("hudder.text_padding.xoffset_left.tooltip"))
				.setSaveOperation(b->config.xoffset_left=b)
				.setDefaultValue(1)
				.build());
		text_padding.addOption(IntNGSMCConfigOption.builder(config.xoffset_right, Component.translatable("hudder.text_padding.xoffset_right"))
				.setHoverComponent(Component.translatable("hudder.text_padding.xoffset_right.tooltip"))
				.setSaveOperation(b->config.xoffset_right=b)
				.setDefaultValue(1)
				.build());
		
		
		
		/* Vanilla Hud */
		vanillahud.addOption(BooleanNGSMCConfigOption.builder(config.showInF3, Component.translatable("hudder.vanillahud.f3"))
				.setHoverComponent(Component.translatable("hudder.vanillahud.f3.tooltip"))
				.setSaveOperation(b->config.showInF3=b)
				.setDefaultValue(false)
				.build());
		vanillahud.addOption(BooleanNGSMCConfigOption.builder(config.removegui, Component.translatable("hudder.vanillahud.removehotbar"))
				.setHoverComponent(Component.translatable("hudder.vanillahud.removehotbar.tooltip"))
				.setSaveOperation(b->config.removegui=b)
				.setDefaultValue(false)
				.build());
		vanillahud.addOption(BooleanNGSMCConfigOption.builder(config.removeeffects, Component.translatable("hudder.vanillahud.removeeffects"))
				.setHoverComponent(Component.translatable("hudder.vanillahud.removeeffects.tooltip"))
				.setSaveOperation(b->config.removeeffects=b)
				.setDefaultValue(false)
				.build());
		vanillahud.addOption(BooleanNGSMCConfigOption.builder(config.removeBossBars, Component.translatable("hudder.vanillahud.removebossbars"))
				.setHoverComponent(Component.translatable("hudder.vanillahud.removebossbars.tooltip"))
				.setSaveOperation(b->config.removeBossBars=b)
				.setDefaultValue(false)
				.build());

        
		/* Safety & Performance */
        safety_perf.addOption(IntNGSMCConfigOption.builder(config.methodBuffer, Component.translatable("hudder.safety_perf.method"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.method.tooltip"))
				.setSaveOperation(b->config.methodBuffer=b)
				.setDefaultValue(2)
				.build());
        safety_perf.addOption(BooleanNGSMCConfigOption.builder(config.limitrate, Component.translatable("hudder.safety_perf.limitrate"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.limitrate.tooltip"))
				.setSaveOperation(b->config.limitrate=b)
				.setDefaultValue(true)
				.build());
        safety_perf.addOption(BooleanNGSMCConfigOption.builder(config.autorefresh, Component.translatable("hudder.safety_perf.autorefresh"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.autorefresh.tooltip"))
				.setSaveOperation(b->config.autorefresh=b)
				.setDefaultValue(true)
				.setComponentProvider(enabledDisabled)
				.build());
		safety_perf.addOption(BooleanNGSMCConfigOption.builder(config.unsafeoperations, Component.translatable("hudder.safety_perf.unsafeoperations"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.unsafeoperations.tooltip"))
				.setSaveOperation(b->config.unsafeoperations=b)
				.setDefaultValue(false)
				.setComponentProvider(enabledDisabled)
				.build());
		safety_perf.addOption(BooleanNGSMCConfigOption.builder(config.disableWarnings,
				Component.translatable("hudder.safety_perf.disableWarnings"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.disableWarnings.tooltip"))
				.setSaveOperation(b->config.disableWarnings=b)
				.setDefaultValue(false)
				.build());
		safety_perf.addOption(BooleanNGSMCConfigOption.builder(config.disableHudpackVersionCheck,
				Component.translatable("hudder.safety_perf.disableHudpackVersionCheck"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.disableHudpackVersionCheck.tooltip"))
				.setSaveOperation(b->config.disableHudpackVersionCheck=b)
				.setDefaultValue(false)
				.build());
		
		
		
		/* Hud specific settings */
		if (Hudder.config.getCompiler() instanceof SettingsProvider provider) {
			if (!provider.setupHudSettings(hudsettings)) {
				builder.removeCategory(hudsettings);
			}
		} else {
			builder.removeCategory(hudsettings);
		}
		
		return builder.build();
	}
	
	public static Component getCompilerWarning(CompilerEntry compilerEntry) {
		if (compilerEntry.deprecated()) return Component.translatable(
				"hudder.general.compilertype.deprecated_warning", compilerEntry.display_name());
		if (compilerEntry.unstable()) return Component.translatable(
				"hudder.general.compilertype.unstable_warning", compilerEntry.display_name());
		return null;
	}
	
}
