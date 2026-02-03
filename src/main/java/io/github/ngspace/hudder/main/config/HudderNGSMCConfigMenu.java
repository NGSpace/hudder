package io.github.ngspace.hudder.main.config;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import dev.ngspace.ngsmcconfig.api.NGSMCConfigBuilder;
import dev.ngspace.ngsmcconfig.api.NGSMCConfigCategory;
import dev.ngspace.ngsmcconfig.options.BooleanNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.DoubleNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.HexNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.IntNGSMCConfigOption;
import dev.ngspace.ngsmcconfig.options.StringNGSMCConfigOption;
import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.utils.HudFileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HudderNGSMCConfigMenu { private HudderNGSMCConfigMenu() {}
	
	protected static Minecraft mc = Minecraft.getInstance();

	public static Screen createMenu(Screen parent) {
		HudderConfig config = Hudder.config;
		
		var builder = new NGSMCConfigBuilder(parent);
		builder.setWriteOperation(() -> {
			try {
				config.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		builder.setWikiUri(URI.create("https://ngspace.dev/hudder"));
		builder.setConfigFile(new File(HudFileUtils.FOLDER));
		
		
		
		// General
		NGSMCConfigCategory general = builder.createCategory(Component.translatable("hudder.general"));
		NGSMCConfigCategory text = builder.createCategory(Component.translatable("hudder.text"));
		NGSMCConfigCategory advanced = builder.createCategory(Component.translatable("hudder.advanced"));
//		NGSMCConfigCategory variables = builder.createCategory(Component.translatable("hudder.global_variables"));
		
		
		
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
					if (!HudFileUtils.exists(val))
						return Component.translatable("hudder.general.mainfile.error");
					return null;
				})
				.build());
		general.addOption(DoubleNGSMCConfigOption.builder(config.scale, Component.translatable("hudder.general.scale"))
				.setHoverComponent(Component.translatable("hudder.general.scale.tooltip"))
				.setSaveOperation(b->config.scale=b.floatValue())
				.setDefaultValue(1d)
				.build());
		
		
		
		/* Text */
		text.addOption(HexNGSMCConfigOption.builder(config.color, Component.translatable("hudder.text.color"))
				.setHoverComponent(Component.translatable("hudder.text.color.tooltip"))
				.setDefaultValue(0xFFd6d6d6)
				.setSaveOperation(b->config.color=b)
				.build());
		text.addOption(HexNGSMCConfigOption.builder(config.backgroundcolor,
				Component.translatable("hudder.text.backgroundcolor"))
				.setHoverComponent(Component.translatable("hudder.text.backgroundcolor.tooltip"))
				.setDefaultValue(0x86353535)
				.setSaveOperation(b->config.backgroundcolor=b)
				.build());
		text.addOption(BooleanNGSMCConfigOption.builder(config.background, Component.translatable("hudder.text.background"))
				.setHoverComponent(Component.translatable("hudder.text.background.tooltip"))
				.setSaveOperation(b->config.background=b)
				.setDefaultValue(true)
				.build());
		text.addOption(BooleanNGSMCConfigOption.builder(config.shadow, Component.translatable("hudder.text.shadow"))
				.setHoverComponent(Component.translatable("hudder.text.shadow.tooltip"))
				.setSaveOperation(b->config.shadow=b)
				.setDefaultValue(true)
				.build());
		text.addOption(IntNGSMCConfigOption.builder(config.yoffset, Component.translatable("hudder.text.yoffset"))
				.setHoverComponent(Component.translatable("hudder.text.yoffset.tooltip"))
				.setSaveOperation(b->config.yoffset=b)
				.setDefaultValue(1)
				.build());
		text.addOption(IntNGSMCConfigOption.builder(config.xoffset, Component.translatable("hudder.text.xoffset"))
				.setHoverComponent(Component.translatable("hudder.text.xoffset.tooltip"))
				.setSaveOperation(b->config.xoffset=b)
				.setDefaultValue(1)
				.build());
		text.addOption(IntNGSMCConfigOption.builder(config.lineHeight, Component.translatable("hudder.text.height"))
				.setHoverComponent(Component.translatable("hudder.text.height.tooltip"))
				.setSaveOperation(b->config.lineHeight=b)
				.setDefaultValue(10)
				.build());
		
		
		
		/* Advanced */
		advanced.addOption(IntNGSMCConfigOption.builder(config.methodBuffer, Component.translatable("hudder.advanced.method"))
				.setHoverComponent(Component.translatable("hudder.advanced.method.tooltip"))
				.setSaveOperation(b->config.methodBuffer=b)
				.setDefaultValue(2)
				.build());
		advanced.addOption(BooleanNGSMCConfigOption.builder(config.showInF3, Component.translatable("hudder.advanced.f3"))
				.setHoverComponent(Component.translatable("hudder.advanced.f3.tooltip"))
				.setSaveOperation(b->config.showInF3=b)
				.setDefaultValue(false)
				.build());
		advanced.addOption(StringNGSMCConfigOption.builder(config.getCompilerName(),
					Component.translatable("hudder.advanced.compilertype"))
	    		.setHoverComponent(Component.translatable("hudder.advanced.compilertype.tooltip"))
	    		.setDefaultValue("hudder")
	    		.setSaveOperation(b->config.setCompilerName(b.toLowerCase()))
	    		.setValidator(e->!Compilers.has(e.toLowerCase())?Component.translatable("hudder.advanced.compilertype.error"):null)
	    		.build());
		advanced.addOption(BooleanNGSMCConfigOption.builder(config.unsafeoperations, Component.translatable("hudder.advanced.unsafeoperations"))
				.setHoverComponent(Component.translatable("hudder.advanced.unsafeoperations.tooltip"))
				.setSaveOperation(b->config.unsafeoperations=b)
				.setDefaultValue(false)
				.build());
		advanced.addOption(BooleanNGSMCConfigOption.builder(config.removegui, Component.translatable("hudder.advanced.removehotbar"))
				.setHoverComponent(Component.translatable("hudder.advanced.removehotbar.tooltip"))
				.setSaveOperation(b->config.removegui=b)
				.setDefaultValue(false)
				.build());
		advanced.addOption(BooleanNGSMCConfigOption.builder(config.limitrate, Component.translatable("hudder.advanced.limitrate"))
				.setHoverComponent(Component.translatable("hudder.advanced.limitrate.tooltip"))
				.setSaveOperation(b->config.limitrate=b)
				.setDefaultValue(true)
				.build());
		
		
		

//		variables.addOption(BooleanNGSMCConfigOption.builder(Component.translatable("hudder.global_variables"),
//						config.globalVariablesEnabled)
//				.setHoverComponent(Component.translatable("hudder.global_variables.enabled.tooltip"))
//				.setSaveOperation(b->config.globalVariablesEnabled=b)
//				.setDefaultValue(true)
//				.build());
		
		return builder.build();
	}
	
}
