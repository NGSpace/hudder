package dev.ngspace.hudder.config;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.utils.Compilers;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigBuilder;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import dev.ngspace.ngsmcconfig.options.BooleanNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.DoubleNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.HexNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.IntNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.StringNGSMCConfigOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HudderNGSMCConfigMenu { private HudderNGSMCConfigMenu() {}
	
	protected static Minecraft mc = Minecraft.getInstance();

	public static Screen createMenu(Screen parent) {
		HudderUserSettings config = Hudder.config.userSettings;
		
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
		
		
		
		// General
		NGSMCConfigCategory general = builder.createCategory(Component.translatable("hudder.general"));
		NGSMCConfigCategory text_rendering = builder.createCategory(Component.translatable("hudder.text_rendering"));
		NGSMCConfigCategory text_padding = builder.createCategory(Component.translatable("hudder.text_padding"));
		NGSMCConfigCategory vanillahud = builder.createCategory(Component.translatable("hudder.vanillahud"));
		NGSMCConfigCategory safety_perf = builder.createCategory(Component.translatable("hudder.safety_perf"));
		
		
		
		NGSMCConfigCategory hudsettings = builder.createCategory(Component.translatable("hudder.hudsettings",
				config.mainfile));
		
		
		
		/* General */
		general.addOption(BooleanNGSMCConfigOption.builder(config.enabled, Component.translatable("hudder.general.enabled"))
				.setHoverComponent(Component.translatable("hudder.general.enabled.tooltip"))
				.setDefaultValue(true)
				.setSaveOperation(b->config.enabled=b)
				.build());
		general.addOption(StringNGSMCConfigOption.builder(config.mainfile, Component.translatable("hudder.general.mainfile"))
				.setHoverComponent(Component.translatable("hudder.general.mainfile.tooltip"))
				.setDefaultValue("hud.hud")
				.setSaveOperation(s->config.mainfile=s)
				.setValidator(val->{
					try {
						if (!HudFileUtils.exists(val))
							return Component.translatable("hudder.general.mainfile.error");
					} catch (SecurityException | IOException e) {
						e.printStackTrace();
					}
					return null;
				})
				.build());
		general.addOption(StringNGSMCConfigOption.builder(Hudder.config.compilerName(),
				Component.translatable("hudder.general.compilertype"))
    		.setHoverComponent(Component.translatable("hudder.general.compilertype.tooltip"))
    		.setDefaultValue("hudder")
    		.setSaveOperation(b->Hudder.config.setCompilerName(b.toLowerCase()))
    		.setValidator(e->!Compilers.has(e.toLowerCase())?Component.translatable("hudder.general.compilertype.error"):null)
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
				.build());
		text_rendering.addOption(BooleanNGSMCConfigOption.builder(config.background, Component.translatable("hudder.text_rendering.background"))
				.setHoverComponent(Component.translatable("hudder.text_rendering.background.tooltip"))
				.setSaveOperation(b->config.background=b)
				.setDefaultValue(true)
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
				.build());
		safety_perf.addOption(BooleanNGSMCConfigOption.builder(config.unsafeoperations, Component.translatable("hudder.safety_perf.unsafeoperations"))
				.setHoverComponent(Component.translatable("hudder.safety_perf.unsafeoperations.tooltip"))
				.setSaveOperation(b->config.unsafeoperations=b)
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
