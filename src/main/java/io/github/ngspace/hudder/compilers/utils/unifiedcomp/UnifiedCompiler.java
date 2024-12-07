package io.github.ngspace.hudder.compilers.utils.unifiedcomp;

import java.io.IOException;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.abstractions.ATextCompiler;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.data_management.BooleanData;
import io.github.ngspace.hudder.data_management.ComponentsData;
import io.github.ngspace.hudder.data_management.NumberData;
import io.github.ngspace.hudder.data_management.ObjectData;
import io.github.ngspace.hudder.data_management.StringData;
import io.github.ngspace.hudder.hudder.HudCompilationManager;
import io.github.ngspace.hudder.uielements.ColorVerticesElement;
import io.github.ngspace.hudder.uielements.GameHudElement;
import io.github.ngspace.hudder.uielements.GameHudElement.GuiType;
import io.github.ngspace.hudder.uielements.TextElement;
import io.github.ngspace.hudder.uielements.TextureElement;
import io.github.ngspace.hudder.uielements.TextureVerticesElement;
import io.github.ngspace.hudder.utils.HudFileUtils;
import io.github.ngspace.hudder.utils.ObjectWrapper;
import io.github.ngspace.hudder.utils.ValueGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * This is my attempt at unifying the Hudder and JavaScript compilers.<br>
 * 
 * Don't use this unless you know what you are doing.
 */
public class UnifiedCompiler {private UnifiedCompiler() {}
	
	public static UnifiedCompiler instance = new UnifiedCompiler();
	public static Minecraft mc = Minecraft.getInstance();
	
	public void applyConsumers(ConsumerBinder binder) {
		//Vertex

		binder.bindConsumer((e,a,l,ch,s)->e.addElem(new ColorVerticesElement(s[0].asFloatArray(),s[1].asLong(),false)),"colorvertices");
		binder.bindConsumer((e,a,l,ch,s)->e.addElem(new ColorVerticesElement(s[0].asFloatArray(),s[1].asLong(),true)),"colorvertices_con");
		
		binder.bindConsumer((e,a,l,ch,s)->{
			try {
				e.addElem(new TextureVerticesElement(s[0].asString(),s[1].asFloatArray(),s[2].asFloatArray(), false));
			} catch (IOException ex) {
				throw new CompileException(ex.getMessage(), l, ch, ex);
			}
		}, "texturevertices");
		binder.bindConsumer((e,a,l,ch,s)->{
			try {
				e.addElem(new TextureVerticesElement(s[0].asString(),s[1].asFloatArray(),s[2].asFloatArray(), true));
			} catch (IOException ex) {
				throw new CompileException(ex.getMessage(), l, ch, ex);
			}
		}, "texturevertices_con");
		
		
		//Textures
		
		binder.bindConsumer((e,a,l,ch,s)->e.addElem(new TextureElement(ResourceLocation.tryParse(s[0].asString().trim()),
				s[1].asInt(), s[2].asInt(), s[3].asInt(),s[4].asInt())), "drawTexture", "texture");
		
		binder.bindConsumer((e,a,l,ch,s)-> {
			try {
				ResourceLocation id = ResourceLocation.withDefaultNamespace(s[0].asString().trim().toLowerCase());
				HudFileUtils.getAndRegisterImage(s[0].asString(),id);
				e.addElem(new TextureElement(id,s[1].asInt(),s[2].asInt(),s[3].asInt(),s[4].asInt()));
			} catch (IOException ex) {throw new CompileException("File "+s[0].asString()+"does not exist");}
		}, "drawLocalTexture", "drawPNG", "drawImage", "image", "png");
		
		//Text
		
		binder.bindConsumer((e,a,l,ch,args) -> {
			int x = args[0].asInt();
			int y = args[1].asInt();

			String text = args[2].asString();
			float scale = (float) (args.length>3 ? args[3].asDouble() : Hudder.config.scale);

			int color = args.length>4 ? args[4].asInt() : Hudder.config.color;
			boolean shadow = args.length>5 ? args[5].asBoolean(): Hudder.config.shadow;
			boolean bg = args.length>6 ? args[6].asBoolean(): Hudder.config.background;
			double bgcolor = args.length>7 ? args[7].asDouble() : Hudder.config.backgroundcolor;
			
			e.addElem(new TextElement(x,y,text,scale,color,shadow,bg,(long) bgcolor));
		}, "drawText", "text");
		
		//GUI
		
		binder.bindConsumer((e,a,l,ch,s)->e.addElem(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.STATUS_BARS)),
				"drawStatusBars", "statusbars");
		binder.bindConsumer((e,a,l,ch,s)->e.addElem(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.EXP_AND_MOUNT_BAR)),
				"drawExpAndMountBars", "xpbar");
		binder.bindConsumer((e,a,l,ch,s)->e.addElem(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.HOTBAR)),
				"drawHotbar", "hotbar");
		binder.bindConsumer((e,a,l,ch,s)->e.addElem(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.ITEM_TOOLTIP)),
				"drawItemTooltip", "helditemtooltip");
		
		//Variables
		
		binder.bindConsumer((e,a,l,ch,s)->a.put(s[0].asString(), s[1]), "set", "setVal", "setVariable");
//		binder.bindConsumer((e,a,l,ch,s)->a.getConfig().savedVariables.put(s[0].asString(),s[1]),"saveVal");
		
		//Logging
		
		binder.bindConsumer((e,a,l,ch,s)->Hudder.alert(s[0].get().toString()), "alert");
		binder.bindConsumer((e,a,l,ch,s)->Hudder.log(s[0].get().toString()), "log");
		binder.bindConsumer((e,a,l,ch,s)->Hudder.warn(s[0].get().toString()), "warn");
		binder.bindConsumer((e,a,l,ch,s)->Hudder.error(s[0].get().toString()), "error");
	}
	
	
	
	public void applyFunctions(FunctionBinder binder) {
		
		//Getters
		
		binder.bindFunction((m,c,s)->c.getVariable(s[0].asString()), "get", "getVal", "getVariable");
		binder.bindFunction((m,c,s)->NumberData.getNumber  (s[0].asString()), "getNumber" );
		binder.bindFunction((m,c,s)->StringData.getString  (s[0].asString()), "getString" );
		binder.bindFunction((m,c,s)->ObjectData.getObject  (s[0].asString()), "getObject" );
		binder.bindFunction((m,c,s)->BooleanData.getBoolean(s[0].asString()), "getBoolean");
		
		binder.bindFunction((m,c,s)->new TranslatedItemStack(mc.player.getInventory().getItem(s[0].asInt())), "getItem");
		
//		binder.bindFunction((m,c,s)->c.getConfig().savedVariables.get(s[0].asString()),"readVal");
		
		//Compile
		
		binder.bindFunction((m,c,s)-> {
			try {
				var e = m.toElementArray();
				
				ATextCompiler ecompiler = Compilers.getCompilerFromName(s[1].asString());
				for (var i : HudCompilationManager.precomplistners) i.accept(ecompiler);
				
				HudInformation result = ecompiler.compile(Hudder.config,HudFileUtils.getFile(s[0].asString()),s[0].asString());

				for (var v : result.elements) m.addElem(v);
				for (var v : e) m.addElem(v);
				
				for (var i : HudCompilationManager.postcomplistners) i.accept(ecompiler);
				return result;
			} catch (ReflectiveOperationException | IOException e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				throw new IllegalArgumentException("Unknown compiler");
			}
		}, "compile", "run", "execute");
		
		
		//Misc
		
		binder.bindFunction((m,c,s)->HudFileUtils.exists(s[0].asString()),"exists");
		binder.bindFunction((m,c,s)->mc.font.width(s[0].asString()), "strWidth", "strwidth");
		binder.bindFunction((m,c,s)->s[0].get().toString(), "toString");
	}
	
	

	@FunctionalInterface public interface BindableConsumer {
		public void invoke(IElementManager man, ATextCompiler comp, int line, int charpos, ObjectWrapper... args) throws CompileException;
	}
	@FunctionalInterface public interface ConsumerBinder {
		public void bindConsumer(BindableConsumer cons, String... names);
	}
	
	

	@FunctionalInterface public interface BindableFunction {
		public Object invoke(IElementManager man, ATextCompiler comp, ObjectWrapper... args) throws CompileException;
	}
	@FunctionalInterface public interface FunctionBinder {
		public void bindFunction(BindableFunction cons, String... names);
	}
	

	public static class TranslatedItemStack implements ValueGetter {
		public String name;
		public int count;
		public int maxcount;
		public int durability;
		public int maxdurability;
		private DataComponentMap components;
		public TranslatedItemStack(ItemStack stack) {
			name = stack.getDisplayName().getString();
			count = stack.getCount();
			maxcount = stack.getMaxStackSize();
			durability = stack.getMaxDamage()-stack.getDamageValue();
			maxdurability = stack.getMaxDamage();
			components = stack.getComponents();
		}
		@Override public String toString() {
			return "{name:\"" + name + "\", count:" + count + ", maxcount: " + maxcount + ", durability: " + durability
					+ ", maxdurability: " + maxdurability + "}";
		}
		@Override public Object get(String component) {
			return ComponentsData.getObject(component, components);
		}
	}
}
