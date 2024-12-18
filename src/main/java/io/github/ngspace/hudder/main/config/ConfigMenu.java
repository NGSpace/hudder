package io.github.ngspace.hudder.main.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.utils.HudFileUtils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class ConfigMenu implements ConfigScreenFactory<Screen> {
	
	protected static Minecraft mc = Minecraft.getInstance();
	
	public static final String URL = "https://ngspace.github.io/hudder";
	HudderConfig config = Hudder.config;
	
	@Override public Screen create(Screen parent) {
		
//		ConfigBuilder builder = new ConfigBuilderImpl() {
//		    @Override
//		    @SuppressWarnings("unchecked")
//			public Screen build() {
//				try {
//					var f = ConfigBuilderImpl.class.getDeclaredField("fallbackCategory");
//					
//					f.setAccessible(true);
//			    	String fallbackCategory = (String) f.get(this);
//			    	
//			    	f = ConfigBuilderImpl.class.getDeclaredField("categoryMap");
//					f.setAccessible(true);
//			    	Map<String, ConfigCategory> categoryMap = (Map<String, ConfigCategory>) f.get(this);
//			    	
//			        
//			        AbstractConfigScreen screen;
//		            screen = new ClothConfigScreen(parent, getTitle(), categoryMap, getDefaultBackgroundTexture()) {
//		            	
//		            };
//			        screen.setSavingRunnable(getSavingRunnable());
//			        screen.setEditable(isEditable());
//			        screen.setFallbackCategory(fallbackCategory == null ? null : Component.literal(fallbackCategory));
//			        screen.setTransparentBackground(hasTransparentBackground());
//			        screen.setAlwaysShowTabs(isAlwaysShowTabs());
//			        screen.setConfirmSave(doesConfirmSave());
//			        screen.setAfterInitConsumer(getAfterInitConsumer());
//			        return screen;
//				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
//						| SecurityException e) {
//					e.printStackTrace();
//					return null;
//				}
//		    }
//		}
		ConfigBuilder builder = ConfigBuilder.create()
			.setTitle(Component.literal("Hudder"))
			.setSavingRunnable(() -> {
				try {
					config.save();
				} catch (IOException e) {
					e.printStackTrace();
					Hudder.showToast(Component.literal("Failed to save hudder config"), Component.literal(e.getMessage()));
				}
			})
			.setDefaultBackgroundTexture(ResourceLocation.tryParse("textures/block/dark_oak_planks.png"))
			.setTransparentBackground(true)
			.setEditable(true)
			.setParentScreen(parent);
		
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		entryBuilder.setResetButtonKey(Component.translatable("hudder.reset"));
		ConfigCategory general = builder.getOrCreateCategory(Component.translatable("hudder.general"));
		ConfigCategory text = builder.getOrCreateCategory(Component.translatable("hudder.text"));
		ConfigCategory advanced = builder.getOrCreateCategory(Component.translatable("hudder.advanced"));
		ConfigCategory variables = builder.getOrCreateCategory(Component.translatable("hudder.global_variables"));
		Function<Boolean, Component> yesno = b->Component.translatable("hudder."+b);
		
		
		/* General */
		//Why am I doing this to myself?
//		AbstractConfigListEntry<String> abstractConfigEntry = new AbstractConfigListEntry<String>(Component.literal("r"), false) {
//
//			@Override public String getValue() {return "r";}
//			@Override public List<? extends GuiEventListener> children() {return new ArrayList<GuiEventListener>();}
//			@Override public Optional<String> getDefaultValue() {return Optional.of("r");}
//			
//			ResourceLocation highlighted = ResourceLocation.withDefaultNamespace("widget/button_highlighted");
//			ResourceLocation not_highlighted = ResourceLocation.withDefaultNamespace("widget/button");
//			
//			@SuppressWarnings("unchecked") @Override public List narratables() {return new ArrayList<>();}
//			public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
//				super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
////				context.blit(RenderType::guiTextured,ResourceLocation.withDefaultNamespace("widget/button"),
////						x, y, 0, 0f, 100, 7, entryWidth, entryHeight);
////				var b = new Button.Builder(getFieldName(), bu->{});
////				b.build().renderWidget(context, mouseX, mouseY, delta);
//				context.blitSprite(
//						RenderType::guiTextured,
//						isHovered ? highlighted : not_highlighted,
//						x,
//						y,
//						entryWidth,
//						entryHeight
//					);
//			}
//			public boolean mouseClicked(double d, double e, int i) {
//				boolean clicked = super.mouseClicked(d, e, i);
//				Hudder.log(d);
//				Hudder.log(e);
//				Hudder.log(i);
//				Hudder.log(this.isMouseOver(d, e));
//				return clicked;
//			}
//		};
//		general.addEntry(abstractConfigEntry);
		general.addEntry(entryBuilder
				.startTextDescription(Component.translatable("hudder.general.folder").withStyle(s -> s
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
						Component.translatable("hudder.general.folder.tooltip")))
				.withClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, HudFileUtils.FOLDER)))).build());
		general.addEntry(entryBuilder
				.startTextDescription(Component.translatable("hudder.general.wiki").withStyle(s -> s
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
						Component.translatable("hudder.general.wiki.tooltip")))
				.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URL)))).build());
		general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("hudder.general.enabled"),config.enabled)
				.setSaveConsumer(b->config.enabled=b)
				.setYesNoTextSupplier(yesno)
				.setTooltip(Component.translatable("hudder.general.enabled.tooltip"))
				.build());
		general.addEntry(entryBuilder.startStrField(
				Component.translatable("hudder.general.mainfile"), config.mainfile)
				.setTooltip(Component.translatable("hudder.general.mainfile.tooltip"))
				.setDefaultValue("hud")
				.setSaveConsumer(b->config.mainfile=b)
				.setErrorSupplier(e->{
					if (!new File(HudFileUtils.FOLDER + e).exists())
						return Optional.of(Component.translatable("hudder.general.mainfile.error"));
					return Optional.empty();
				})
				.build());
		general.addEntry(entryBuilder.startFloatField(Component.translatable("hudder.general.scale"), config.scale)
				.setTooltip(Component.translatable("hudder.general.scale.tooltip"))
				.setSaveConsumer(b->config.scale=b)
				.setDefaultValue(1f)
				.build());
		general.addEntry(entryBuilder
				.startTextDescription(Component.translatable("hudder.general.folder").withStyle(s -> s
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
						Component.translatable("hudder.general.folder.tooltip")))
				.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, HudFileUtils.FOLDER)))).build());
		
		
		/* Text */
		text.addEntry(entryBuilder.startColorField(Component.translatable("hudder.text.color"), config.color)
				.setTooltip(Component.translatable("hudder.text.color.tooltip"))
				.setAlphaMode(true)
				.setDefaultValue(0xd6d6d6)
				.setSaveConsumer(b->config.color=b)
				.build());
		text.addEntry(entryBuilder.startColorField(Component.translatable("hudder.text.backgroundcolor"),
				config.backgroundcolor)
				.setTooltip(Component.translatable("hudder.text.backgroundcolor.tooltip"))
				.setAlphaMode(true)
				.setDefaultValue(0x86353535)
				.setSaveConsumer(b->config.backgroundcolor=b)
				.build());
		text.addEntry(entryBuilder.startBooleanToggle(Component.translatable("hudder.text.background"), config.background)
				.setTooltip(Component.translatable("hudder.text.background.tooltip"))
				.setSaveConsumer(b->config.background=b)
				.setYesNoTextSupplier(yesno)
				.setDefaultValue(true)
				.build());
		text.addEntry(entryBuilder.startBooleanToggle(Component.translatable("hudder.text.shadow"), config.shadow)
				.setTooltip(Component.translatable("hudder.text.shadow.tooltip"))
				.setSaveConsumer(b->config.shadow=b)
				.setYesNoTextSupplier(yesno)
				.setDefaultValue(true)
				.build());
		text.addEntry(entryBuilder.startIntField(Component.translatable("hudder.text.yoffset"), config.yoffset)
				.setTooltip(Component.translatable("hudder.text.yoffset.tooltip"))
				.setSaveConsumer(b->config.yoffset=b)
				.setDefaultValue(1)
				.build());
		text.addEntry(entryBuilder.startIntField(Component.translatable("hudder.text.xoffset"), config.xoffset)
				.setTooltip(Component.translatable("hudder.text.xoffset.tooltip"))
				.setSaveConsumer(b->config.xoffset=b)
				.setDefaultValue(1)
				.build());
		text.addEntry(entryBuilder.startIntField(Component.translatable("hudder.text.height"), config.lineHeight)
				.setTooltip(Component.translatable("hudder.text.height.tooltip"))
				.setSaveConsumer(b->config.lineHeight=b)
				.setDefaultValue(10)
				.build());
		
		/* Advanced */
		advanced.addEntry(entryBuilder.startIntField(Component.translatable("hudder.advanced.method"), config.methodBuffer)
				.setTooltip(Component.translatable("hudder.advanced.method.tooltip"))
				.setSaveConsumer(b->config.methodBuffer=b)
				.setDefaultValue(2)
				.build());
		advanced.addEntry(entryBuilder.startBooleanToggle(Component.translatable("hudder.advanced.f3"), config.showInF3)
				.setTooltip(Component.translatable("hudder.advanced.f3.tooltip"))
				.setSaveConsumer(b->config.showInF3=b)
				.setYesNoTextSupplier(yesno)
				.setDefaultValue(false)
				.build());
		advanced.addEntry(entryBuilder.startStrField(Component.translatable("hudder.advanced.compilertype"), 
				config.compilertype)
	    		.setTooltip(Component.translatable("hudder.advanced.compilertype.tooltip"))
	    		.setDefaultValue("hudder")
	    		.setSaveConsumer(b->config.setCompiler(b.toLowerCase()))
	    		.setErrorSupplier(e->!Compilers.has(e.toLowerCase())?
	    				Optional.of(Component.translatable("hudder.advanced.compilertype.error")):Optional.empty())
	    		.build());
		advanced.addEntry(
				entryBuilder.startBooleanToggle(Component.translatable("hudder.advanced.javascript"), config.javascript)
				.setTooltip(Component.translatable("hudder.advanced.javascript.tooltip"))
				.setSaveConsumer(b->config.javascript=b)
				.setYesNoTextSupplier(yesno)
				.setDefaultValue(false)
				.build());
		
		
		

		variables.addEntry(entryBuilder
				.startBooleanToggle(Component.translatable("hudder.global_variables"),
						config.globalVariablesEnabled)
				.setTooltip(Component.translatable("hudder.global_variables.enabled.tooltip"))
				.setSaveConsumer(b->config.globalVariablesEnabled=b)
				.setYesNoTextSupplier(yesno)
				.setDefaultValue(true)
				.build());
		variables.addEntry(new NestedListListEntry<Variable, MultiElementListEntry<Variable>>(
				Component.translatable("hudder.global_variables"),
                getVarList(),
                true, 
                () -> Optional.of(new Component[]{Component.translatable("hudder.global_variables.tooltip")}),
                this::makelist,
                () -> new ArrayList<Variable>(),
                entryBuilder.getResetButtonKey(),
                true,
                false,
                (varia, listListEntry) -> createEntry(entryBuilder, (varia!=null?varia:new Variable("","")))));
		
		
		//VER 3.0.0
		

		advanced.addEntry(
				entryBuilder.startBooleanToggle(Component.translatable("hudder.advanced.removehotbar"),config.removegui)
				.setTooltip(Component.translatable("hudder.advanced.removehotbar.tooltip"))
				.setSaveConsumer(b->config.removegui=b)
				.setYesNoTextSupplier(yesno)
				.setDefaultValue(false)
				.build());
		advanced.addEntry(
				entryBuilder.startBooleanToggle(Component.translatable("hudder.advanced.limitrate"),config.limitrate)
				.setTooltip(Component.translatable("hudder.advanced.limitrate.tooltip"))
				.setSaveConsumer(b->config.limitrate=b)
				.setYesNoTextSupplier(yesno)
				.setDefaultValue(true)
				.build());
		
		return builder.build();
	}
    private List<Variable> getVarList() {
    	ArrayList<Variable> lst = new ArrayList<Variable>();
    	for (Map.Entry<String, Object> v : config.globalVariables.entrySet())
    		lst.add(new Variable(v.getKey(),v.getValue()));
		return lst;
	}
	private static MultiElementListEntry<Variable> createEntry(ConfigEntryBuilder entryBuilder, Variable variable) {
		return new MultiElementListEntry<Variable>(
			Component.keybind(!"".equals(variable.key) ? variable.key : "Variable" ),
            variable,
            Arrays.asList(
                entryBuilder.startTextField(
                		Component.translatable("hudder.global_variables.key"),
                        variable.key)
                    .setTooltip(Component.translatable("hudder.global_variables.key.tooltip"))
                    .setSaveConsumer(name -> variable.key = name)
                    .build(),
                entryBuilder.startStrField(
                		Component.translatable("hudder.global_variables.value"),
                        variable.value.toString())
                    .setTooltip(Component.translatable("hudder.global_variables.value.tooltip"))
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
		config.globalVariables = new HashMap<String,Object>();
		for (Variable l : list) config.globalVariables.put(l.key, l.value);
	}
}
