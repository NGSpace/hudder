package io.github.ngspace.hudder.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.ATextCompiler;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
//import net.minecraft.chat.Component;
import me.shedaniel.math.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

public class ConfigMenu implements ConfigScreenFactory<Screen> {
	
	@Override public Screen create(Screen parent) {
		ConfigInfo config = ConfigManager.getConfig();
		
		ConfigBuilder builder = ConfigBuilder.create()
			.setTitle(Text.translatable(Hudder.MOD_ID))
			.setSavingRunnable(config::saveConfig)
			.setTransparentBackground(true)
			.setEditable(true)
			.setParentScreen(parent);

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		entryBuilder.setResetButtonKey(Text.translatable("hudder.reset"));
		ConfigCategory general = builder.getOrCreateCategory(Text.translatable("hudder.general"));
		ConfigCategory text = builder.getOrCreateCategory(Text.translatable("hudder.text"));
		ConfigCategory advanced = builder.getOrCreateCategory(Text.translatable("hudder.advanced"));
		ConfigCategory variables = builder.getOrCreateCategory(Text.translatable("hudder.global_variables"));
		Function<Boolean, Text> yesno = b->Text.translatable("hudder."+b);
		general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("hudder.general.enabled"),config.enabled)
				.setSaveConsumer(b -> config.enabled = b)
				.setYesNoTextSupplier(yesno)
				.setTooltip(Text.translatable("hudder.general.enabled.tooltip"))
				.build());
		
		
		/* General */
		general.addEntry(entryBuilder.startStrField(
				Text.translatable("hudder.general.mainfile"), config.mainfile)
				.setTooltip(Text.translatable("hudder.general.mainfile.tooltip"))
				.setDefaultValue("hud")
				.setSaveConsumer(b -> config.mainfile = b)
				.setErrorSupplier(e->{
					if (!new File(ConfigInfo.FOLDER + e).exists()) return 
							Optional.of(Text.translatable("hudder.general.mainfile.error"));
					return Optional.empty();
				})
				.build());
		general.addEntry(entryBuilder.startFloatField(Text.translatable("hudder.general.scale"), config.scale)
				.setTooltip(Text.translatable("hudder.general.scale.tooltip"))
				.setSaveConsumer(b -> config.scale = b)
				.setDefaultValue(1f)
				.build());
		general.addEntry(entryBuilder
				.startTextDescription(Text.translatable("hudder.general.folder").styled(s -> s
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
						Text.translatable("hudder.general.folder.tooltip")))
				.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, ConfigInfo.FOLDER)))).build());
		
		
		/* Text */
		text.addEntry(entryBuilder.startColorField(Text.translatable("hudder.text.color"), getColor(config.color))
				.setTooltip(Text.translatable("hudder.text.color.tooltip"))
				.setAlphaMode(false)
				.setDefaultValue(config.color)
				.setSaveConsumer(b -> config.color = b)
				.build());
		text.addEntry(entryBuilder.startBooleanToggle(Text.translatable("hudder.text.shadow"), config.shadow)
				.setTooltip(Text.translatable("hudder.text.shadow.tooltip"))
				.setSaveConsumer(b -> config.shadow = b)
				.setYesNoTextSupplier(yesno)
				.setDefaultValue(true)
				.build());
		text.addEntry(entryBuilder.startIntField(Text.translatable("hudder.text.yoffset"), config.yoffset)
				.setTooltip(Text.translatable("hudder.text.yoffset.tooltip"))
				.setSaveConsumer(b -> config.yoffset = b)
				.setDefaultValue(1)
				.build());
		text.addEntry(entryBuilder.startIntField(Text.translatable("hudder.text.xoffset"), config.xoffset)
				.setTooltip(Text.translatable("hudder.text.xoffset.tooltip"))
				.setSaveConsumer(b -> config.xoffset = b)
				.setDefaultValue(1)
				.build());
		text.addEntry(entryBuilder.startIntField(Text.translatable("hudder.text.height"), config.lineHeight)
				.setTooltip(Text.translatable("hudder.text.height.tooltip"))
				.setSaveConsumer(b -> config.lineHeight = b)
				.setDefaultValue(10)
				.build());
		
		/* Advanced */
		advanced.addEntry(entryBuilder.startIntField(Text.translatable("hudder.advanced.meta"), config.metaBuffer)
				.setTooltip(Text.translatable("hudder.advanced.meta.tooltip"))
				.setSaveConsumer(b -> config.metaBuffer = b)
				.setDefaultValue(2)
				.build());
		advanced.addEntry(entryBuilder.startBooleanToggle(Text.translatable("hudder.advanced.f3"), config.showInF3)
				.setTooltip(Text.translatable("hudder.advanced.f3.tooltip"))
				.setSaveConsumer(b -> config.showInF3 = b)
				.setYesNoTextSupplier(yesno)
				.setDefaultValue(false)
				.build());
		advanced.addEntry(entryBuilder.startStrField(Text.translatable("hudder.advanced.compilertype"), 
				config.compilertype)
	    		.setTooltip(Text.translatable("hudder.advanced.compilertype.tooltip"))
	    		.setDefaultValue("Default")
	    		.setSaveConsumer(b -> config.compilertype = b)
	    		.setErrorSupplier(e->ATextCompiler.compilers.get(e)==null?
	    				Optional.of(Text.translatable("hudder.advanced.compilertype.error")):Optional.empty())
	    		.build());
		advanced.addEntry(
				entryBuilder.startBooleanToggle(Text.translatable("hudder.advanced.javascript"), config.javascript)
				.setTooltip(Text.translatable("hudder.advanced.javascript.tooltip"))
				.setSaveConsumer(b -> config.javascript = b)
				.setYesNoTextSupplier(yesno)
				.setDefaultValue(false)
				.build());
		
		
		

		variables.addEntry(entryBuilder
				.startBooleanToggle(Text.translatable("hudder.global_variables"),
						config.globalVariablesEnabled)
				.setTooltip(Text.translatable("hudder.global_variables.enabled.tooltip"))
				.setSaveConsumer(b -> config.globalVariablesEnabled = b)
				.setYesNoTextSupplier(yesno)
				.setDefaultValue(true)
				.build());
		variables.addEntry(new NestedListListEntry<Variable, MultiElementListEntry<Variable>>(
                Text.translatable("hudder.global_variables"), getList(),
                true, 
                () -> Optional.of(new Text[]{Text.translatable("hudder.global_variables.tooltip")}),
                this::makelist,
                () -> new ArrayList<Variable>(),
                entryBuilder.getResetButtonKey(),
                true,
                false,
                (varia, listListEntry) -> createEntry(entryBuilder, (varia!=null?varia:new Variable("","")))));

		return builder.build();
	}
    private List<Variable> getList() {
    	ArrayList<Variable> ar = new ArrayList<Variable>();
    	for (Map.Entry<String, Object> v : ConfigManager.getConfig().globalVariables.entrySet())
    		ar.add(new Variable(v.getKey(),v.getValue()));
		return ar;
	}
	private static MultiElementListEntry<Variable> createEntry(ConfigEntryBuilder entryBuilder, Variable variable) {
		return new MultiElementListEntry<Variable>(
            Text.of(!"".equals(variable.key) ? variable.key : "Variable"),
            variable,
            Arrays.asList(
                entryBuilder.startTextField(
                        Text.translatable("hudder.global_variables.key"),
                        variable != null ? variable.key : "")
                    .setTooltip(Text.translatable("hudder.global_variables.key.tooltip"))
                    .setSaveConsumer(name -> variable.key = name)
                    .build(),
                entryBuilder.startStrField(
                        Text.translatable("hudder.global_variables.value"),
                        variable != null ? variable.value.toString() : "")
                    .setTooltip(Text.translatable("hudder.global_variables.value.tooltip"))
                    .setSaveConsumer(value -> variable.value = value)
                    .build()
            ),
            true);
    }
    
    public static class Variable {
    	public Variable(String key, Object value) {this.key=key;this.value=value;}
		public String key;
    	public Object value;
    }

	private void makelist(List<Variable> list) {
		ConfigManager.getConfig().globalVariables = new HashMap<String,Object>();
		for (Variable l : list) {
			ConfigManager.getConfig().globalVariables.put(l.key, l.value);
		}
	}

	private Color getColor(int argb) {
		int b = (argb)&0xFF;
		int g = (argb>>8)&0xFF;
		int r = (argb>>16)&0xFF;
		return Color.ofRGBA(r, g, b, 0xFF);
	}
}
