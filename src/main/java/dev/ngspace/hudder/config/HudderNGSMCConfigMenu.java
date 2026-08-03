package dev.ngspace.hudder.config;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Comparator;
import java.util.function.Function;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.utils.Compilers;
import dev.ngspace.hudder.compilers.utils.Compilers.CompilerEntry;
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
		HudderUserSettings config = Hudder.config.userSettings;
		
		Function<Boolean, Component> enabledDisabled = v -> Boolean.TRUE.equals(v) ? Component.translatable(
				"hudder.ngsmcconfig.enabled") : Component.translatable("hudder.ngsmcconfig.disabled");
		
		var builder = new NGSMCConfigBuilder(parent);
		builder.setWriteOperation(() -> {
			try {
				Hudder.config.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		builder.setDocsUri(URI.create("https://ngspace.dev/hudder"));
		builder.setConfigFile(new File(HudFileUtils.FOLDER));
		builder.setConfigButtonText(Component.translatable("hudder.ngsmcconfig.config"));
		
		
		
		// Huds
		// NGSMCConfig changes the size and position anyways
		var widget = new HudSelectionList(Minecraft.getInstance(), new File(HudFileUtils.FOLDER), config);
		builder.addCustomWidgetCategory(Component.translatable("hudder.mainfile"),
				new NGSMCConfigIcon.SpriteIcon("items", "item/map"),
				widget, widget::save, widget::reset, widget::error, widget::warning);
		
		
		
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
		general.addOption(BooleanNGSMCConfigOption.fluentBuilder(config.enabled, Component.translatable("hudder.general.enabled"))
				.setHoverComponent(Component.translatable("hudder.general.enabled.tooltip"))
				.setDefaultValue(true)
				.setSaveOperation(b->config.enabled=b)
				.setComponentProvider(enabledDisabled)
				.build());
//		general.addOption(StringNGSMCConfigOption.fluentBuilder(config.mainfile, Component.translatable("hudder.general.mainfile"))
//				.setHoverComponent(Component.translatable("hudder.general.mainfile.tooltip"))
//				.setDefaultValue("hud.hud")
//				.setSaveOperation(s->config.mainfile=s)
//				.setValidator(val->{
//					try {
//						if (!HudFileUtils.exists(val))
//							return Component.translatable("hudder.general.mainfile.error");
//					} catch (SecurityException | IOException e) {
//						e.printStackTrace();
//					}
//					return null;
//				})
//				.setWarningProvider(file->{
//					if (Compilers.getEntryFromDisplayName(comp.get()).unstable())
//						return Component.translatable("hudder.general.compilertype.unstable_warning", comp.get());
//					return Compilers.getCompilerFromDisplayname(comp.get()).isValidFilePath(file)?null:
//						Component.translatable("hudder.general.mainfile.unsupportedformat",comp.get(),file);
//				})
//				.build());
		general.addOption(DropdownNGSMCConfigOption.fluentBuilder(Compilers.getDisplayNameFromCompilerName(Hudder.config.compilerName()),
					Component.translatable("hudder.general.compilertype"),
					Compilers.entries().stream()
						.sorted(Comparator.comparing(CompilerEntry::unstable)
								.thenComparing(CompilerEntry::displayname, String.CASE_INSENSITIVE_ORDER))
						.map(e->e.displayname())
						.toList())
	    		.setHoverComponent(Component.translatable("hudder.general.compilertype.tooltip"))
	    		.setDefaultValue("Hudder")
	    		.setSaveOperation(b->Hudder.config.setCompilerName(Compilers.getCompilerNameFromDisplayname(b)))
	    		.setValidator(e->{
	    			widget.comp = e;
	    			return Compilers.hasCompilerFromDisplayName(e)
	    					? null : Component.translatable("hudder.general.compilertype.error");
	    		})
	    		.setWarningProvider(e->Compilers.getEntryFromDisplayName(e).unstable()
	    				? Component.translatable("hudder.general.compilertype.unstable_warning", e): null)
	    		.build());
		general.addOption(DoubleNGSMCConfigOption.fluentBuilder(config.scale, Component.translatable("hudder.general.scale"))
				.setHoverComponent(Component.translatable("hudder.general.scale.tooltip"))
				.setSaveOperation(b->config.scale=b.floatValue())
				.setDefaultValue(1d)
				.build());
		
		
		
		/* Text */
		text_rendering.addOption(HexNGSMCConfigOption.fluentBuilder(config.color, Component.translatable("hudder.text_rendering.color"))
				.setHoverComponent(Component.translatable("hudder.text_rendering.color.tooltip"))
				.setDefaultValue(0xFFd6d6d6)
				.setSaveOperation(b->config.color=b)
				.build());
		text_rendering.addOption(BooleanNGSMCConfigOption.fluentBuilder(config.shadow, Component.translatable("hudder.text_rendering.shadow"))
				.setHoverComponent(Component.translatable("hudder.text_rendering.shadow.tooltip"))
				.setSaveOperation(b->config.shadow=b)
				.setDefaultValue(true)
				.setComponentProvider(enabledDisabled)
				.build());
		text_rendering.addOption(BooleanNGSMCConfigOption.fluentBuilder(config.background, Component.translatable("hudder.text_rendering.background"))
				.setHoverComponent(Component.translatable("hudder.text_rendering.background.tooltip"))
				.setSaveOperation(b->config.background=b)
				.setDefaultValue(true)
				.setComponentProvider(enabledDisabled)
				.build());
		text_rendering.addOption(HexNGSMCConfigOption.fluentBuilder(config.backgroundcolor,
				Component.translatable("hudder.text_rendering.backgroundcolor"))
				.setHoverComponent(Component.translatable("hudder.text_rendering.backgroundcolor.tooltip"))
				.setDefaultValue(0x86353535)
				.setSaveOperation(b->config.backgroundcolor=b)
				.build());
		text_rendering.addOption(IntNGSMCConfigOption.fluentBuilder(config.lineHeight, Component.translatable("hudder.text_rendering.height"))
				.setHoverComponent(Component.translatable("hudder.text_rendering.height.tooltip"))
				.setSaveOperation(b->config.lineHeight=b)
				.setDefaultValue(10)
				.build());
		
		
		/* Text Padding */
		text_padding.addOption(IntNGSMCConfigOption.fluentBuilder(config.yoffset_top, Component.translatable("hudder.text_padding.yoffset_top"))
				.setHoverComponent(Component.translatable("hudder.text_padding.yoffset_top.tooltip"))
				.setSaveOperation(b->config.yoffset_top=b)
				.setDefaultValue(1)
				.build());
		text_padding.addOption(IntNGSMCConfigOption.fluentBuilder(config.yoffset_bottom, Component.translatable("hudder.text_padding.yoffset_bottom"))
				.setHoverComponent(Component.translatable("hudder.text_padding.yoffset_bottom.tooltip"))
				.setSaveOperation(b->config.yoffset_bottom=b)
				.setDefaultValue(0)
				.build());
		text_padding.addOption(IntNGSMCConfigOption.fluentBuilder(config.xoffset_left, Component.translatable("hudder.text_padding.xoffset_left"))
				.setHoverComponent(Component.translatable("hudder.text_padding.xoffset_left.tooltip"))
				.setSaveOperation(b->config.xoffset_left=b)
				.setDefaultValue(1)
				.build());
		text_padding.addOption(IntNGSMCConfigOption.fluentBuilder(config.xoffset_right, Component.translatable("hudder.text_padding.xoffset_right"))
				.setHoverComponent(Component.translatable("hudder.text_padding.xoffset_right.tooltip"))
				.setSaveOperation(b->config.xoffset_right=b)
				.setDefaultValue(1)
				.build());
		
		
		
		/* Vanilla Hud */
		vanillahud.addOption(BooleanNGSMCConfigOption.fluentBuilder(config.showInF3, Component.translatable("hudder.vanillahud.f3"))
				.setHoverComponent(Component.translatable("hudder.vanillahud.f3.tooltip"))
				.setSaveOperation(b->config.showInF3=b)
				.setDefaultValue(false)
				.build());
		vanillahud.addOption(BooleanNGSMCConfigOption.fluentBuilder(config.removegui, Component.translatable("hudder.vanillahud.removehotbar"))
				.setHoverComponent(Component.translatable("hudder.vanillahud.removehotbar.tooltip"))
				.setSaveOperation(b->config.removegui=b)
				.setDefaultValue(false)
				.build());
		vanillahud.addOption(BooleanNGSMCConfigOption.fluentBuilder(config.removeeffects, Component.translatable("hudder.vanillahud.removeeffects"))
				.setHoverComponent(Component.translatable("hudder.vanillahud.removeeffects.tooltip"))
				.setSaveOperation(b->config.removeeffects=b)
				.setDefaultValue(false)
				.build());

        
		/* Safety & Performance */
        safety_perf.addOption(IntNGSMCConfigOption.fluentBuilder(config.methodBuffer, Component.translatable("hudder.safety_perf.method"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.method.tooltip"))
				.setSaveOperation(b->config.methodBuffer=b)
				.setDefaultValue(2)
				.build());
        safety_perf.addOption(BooleanNGSMCConfigOption.fluentBuilder(config.limitrate, Component.translatable("hudder.safety_perf.limitrate"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.limitrate.tooltip"))
				.setSaveOperation(b->config.limitrate=b)
				.setDefaultValue(true)
				.build());
        safety_perf.addOption(BooleanNGSMCConfigOption.fluentBuilder(config.autorefresh, Component.translatable("hudder.safety_perf.autorefresh"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.autorefresh.tooltip"))
				.setSaveOperation(b->config.autorefresh=b)
				.setDefaultValue(true)
				.setComponentProvider(enabledDisabled)
				.build());
		safety_perf.addOption(BooleanNGSMCConfigOption.fluentBuilder(config.unsafeoperations, Component.translatable("hudder.safety_perf.unsafeoperations"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.unsafeoperations.tooltip"))
				.setSaveOperation(b->config.unsafeoperations=b)
				.setDefaultValue(false)
				.setComponentProvider(enabledDisabled)
				.build());
		safety_perf.addOption(BooleanNGSMCConfigOption.fluentBuilder(config.disableWarnings,
				Component.translatable("hudder.safety_perf.disableWarnings"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.disableWarnings.tooltip"))
				.setSaveOperation(b->config.disableWarnings=b)
				.setDefaultValue(false)
				.build());
		safety_perf.addOption(BooleanNGSMCConfigOption.fluentBuilder(config.disableHudpackVersionCheck,
				Component.translatable("hudder.safety_perf.disableHudpackVersionCheck"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.disableHudpackVersionCheck.tooltip"))
				.setSaveOperation(b->config.disableHudpackVersionCheck=b)
				.setDefaultValue(false)
				.build());
		
		
		
		/* Hud specific settings */
		try {
			if (!Hudder.config.getCompiler().setupHudSettings(hudsettings))
				builder.removeCategory(hudsettings);
		} catch (Exception e) {
			e.printStackTrace();
			builder.removeCategory(hudsettings);
		}
		
		return builder.build();
	}
	
}
