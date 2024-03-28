package io.github.ngspace.hudder.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;

import io.github.ngspace.hudder.Hudder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import me.shedaniel.clothconfig2.impl.ConfigEntryBuilderImpl;
//import net.minecraft.chat.Component;
import me.shedaniel.math.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ConfigMenu implements ConfigScreenFactory<Screen> {
	
	@Override public Screen create(Screen parent) {
		ConfigInfo config = ConfigManager.getConfig();
		ConfigBuilder builder = ConfigBuilder.create()
			.setTitle(Text.of(Hudder.MOD_ID))
			.setSavingRunnable(config::saveConfig)
			.setTransparentBackground(true)
			.setEditable(true)
			.setParentScreen(parent);

		ConfigEntryBuilderImpl entryBuilder = (ConfigEntryBuilderImpl) builder.entryBuilder();
		ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));
		ConfigCategory text = builder.getOrCreateCategory(Text.of("Text"));
		ConfigCategory advanced = builder.getOrCreateCategory(Text.of("Advanced"));
		ConfigCategory variables = builder.getOrCreateCategory(Text.of("Global Variables"));
		general.addEntry(entryBuilder.startBooleanToggle(Text.of("Enabled"), config.enabled)
				.setSaveConsumer(b -> config.enabled = b)
				.setTooltip(Text.of("Whether "+Hudder.MOD_ID+" should be enabled or not (duh)"))
				.build());
		/** Still kinda useless until I get JS to work :P */
//		category.addEntry(entryBuilder.startStrField(Text.of("Complier Type"), config.compilertype)
//	    		.setTooltip(Text.of("Change the type of compiler"))
//	    		.setDefaultValue("Default")
//	    		.setSaveConsumer(b -> config.compilertype = b)
//	    		.setErrorSupplier(e->{
//	    			if (ATextCompiler.compilers.get(e)==null)
//	    				return Optional.of(Text.of("Invalid compiler type"));
//	    			return Optional.empty();
//	    		})
//	    		.build());
		
		/* General */
		general.addEntry(entryBuilder.startStrField(
				Text.literal("Main file"), config.mainfile)
				.setTooltip(Text.literal("Change the main file\n"
						+"\u00A77The file must be located in the \u00A76.minecraft/config/"+Hudder.MOD_ID
						+"\u00A77 folder"))
				.setDefaultValue("hud")
				.setSaveConsumer(b -> config.mainfile = b)
				.setErrorSupplier(e->{
					if (!new File(ConfigInfo.FOLDER + e).exists()) return 
							Optional.of(Text.of("File does not exist in the .minecraft/config/"
									+Hudder.MOD_ID+" folder"));
					return Optional.empty();
				})
				.build());
		general.addEntry(entryBuilder.startFloatField(Text.of("Default scale"), config.scale)
				.setTooltip(Text.of("Control the default scale of meta elements"),
						Text.of("\u00A77This can be overriden by elements specifying the size on their own"))
				.setSaveConsumer(b -> config.scale = b)
				.setDefaultValue(1f)
				.build());
		general.addEntry(entryBuilder
				.startTextDescription(Text.literal("\u00A77The files are located in the \"\u00A76config/"
				+Hudder.MOD_ID+"\u00A77\" folder").styled(s -> s
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to open folder")))
				.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, ConfigInfo.FOLDER)))).build());
		
		
		/* Text */
		text.addEntry(entryBuilder.startColorField(Text.of("Text Color"), getColor(config.color))
				.setTooltip(Text.of("Change the color of the text"))
				.setAlphaMode(false)
				.setDefaultValue(config.color)
				.setSaveConsumer(b -> config.color = b)
				.build());
		text.addEntry(entryBuilder.startBooleanToggle(Text.of("Text shadow"), config.shadow)
				.setTooltip(Text.of("Whether text should have a shadow"))
				.setSaveConsumer(b -> config.shadow = b)
				.setDefaultValue(true)
				.build());
		text.addEntry(entryBuilder.startIntField(Text.of("Text offset - Y"), config.yoffset)
				.setTooltip(Text.of("Control the y offset of the text"))
				.setSaveConsumer(b -> config.yoffset = b)
				.setDefaultValue(1)
				.build());
		text.addEntry(entryBuilder.startIntField(Text.of("Text offset - X"), config.xoffset)
				.setTooltip(Text.of("Control the x offset of the text"))
				.setSaveConsumer(b -> config.xoffset = b)
				.setDefaultValue(1)
				.build());
		text.addEntry(entryBuilder.startIntField(Text.of("Text line height"), config.lineHeight)
				.setTooltip(Text.of("Control the line height of the text"))
				.setSaveConsumer(b -> config.lineHeight = b)
				.setDefaultValue(10)
				.build());
		
		/* Advanced */
		advanced.addEntry(entryBuilder.startIntField(Text.of("Meta line buffer"), config.metaBuffer)
				.setTooltip(Text.of("Control how many newlines should be deleted around meta changes"))
				.setSaveConsumer(b -> config.metaBuffer = b)
				.setDefaultValue(2)
				.build());
		advanced.addEntry(entryBuilder.startBooleanToggle(Text.of("Show in F3"), config.showInF3)
				.setTooltip(Text.of("Whether should still render hud in f3"))
				.setSaveConsumer(b -> config.showInF3 = b)
				.setDefaultValue(false)
				.build());
		
		

		variables.addEntry(entryBuilder
				.startBooleanToggle(Text.of("Global variables"), config.globalVariablesEnabled)
				.setTooltip(Text.of("Whether global variables should be used"))
				.setSaveConsumer(b -> config.globalVariablesEnabled = b)
				.setDefaultValue(true)
				.build());
		variables.addEntry(new NestedListListEntry<Variable, MultiElementListEntry<Variable>>(
                Text.of("Global Variables"), getList(),
                true, 
                () -> Optional.of(new Text[]{
                		Text.of("Control variables and set values without editing the files"),
                		Text.of("\u00A77This can also be used to create parameters for "+Hudder.MOD_ID+" files")}),
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
    	for (var v : ConfigManager.getConfig().globalVariables.entrySet())
    		ar.add(new Variable(v.getKey(),v.getValue()));
		return ar;
	}
	private static MultiElementListEntry<Variable> createEntry(ConfigEntryBuilder entryBuilder, Variable variable) {
		return new MultiElementListEntry<Variable>(
            Text.of(!"".equals(variable.key) ? variable.key : "Variable"),
            variable,
            Arrays.asList(
                entryBuilder.startTextField(
                        Text.of("Key"),
                        variable != null ? variable.key : "")
                    .setTooltip(Text.of("The name of the variable"))
                    .setSaveConsumer(name -> variable.key = name)
                    .build(),
                entryBuilder.startStrField(
                        Text.of("Value"),
                        variable != null ? variable.value.toString() : "")
                    .setTooltip(Text.of("The value of the variable"))
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
//		int a = (argb>>24)&0xFF;
		return Color.ofRGBA(r, g, b, 0xFF);
	}
}
