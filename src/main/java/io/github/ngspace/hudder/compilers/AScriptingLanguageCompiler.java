package io.github.ngspace.hudder.compilers;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.compilers.utils.IScriptingLanguageEngine;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.data_management.BooleanData;
import io.github.ngspace.hudder.data_management.NumberData;
import io.github.ngspace.hudder.data_management.StringData;
import io.github.ngspace.hudder.methods.elements.AUIElement;
import io.github.ngspace.hudder.methods.elements.ColorVerticesElement;
import io.github.ngspace.hudder.methods.elements.GameHudElement;
import io.github.ngspace.hudder.methods.elements.GameHudElement.GuiType;
import io.github.ngspace.hudder.methods.elements.ItemElement;
import io.github.ngspace.hudder.methods.elements.TextElement;
import io.github.ngspace.hudder.methods.elements.TextureElement;
import io.github.ngspace.hudder.methods.elements.TextureVerticesElement;
import io.github.ngspace.hudder.util.HudCompilationManager;
import io.github.ngspace.hudder.util.HudFileUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public abstract class AScriptingLanguageCompiler extends AVarTextCompiler {
	
	protected static MinecraftClient mc = MinecraftClient.getInstance();
	
	protected AScriptingLanguageCompiler() {
		HudCompilationManager.addPreCompilerListener(c->{if(c==this) elms.clear();});
		HudFileUtils.addClearFileCacheListener(()->{
			try {
				for(RuntimeCache c:cache.values())c.close();
			} catch (IOException e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				throw new CompileException(e.getLocalizedMessage());
			}
			cache.clear();
		});
	}
	
	public Map<String, RuntimeCache> cache = new HashMap<String, RuntimeCache>();
	public List<AUIElement> elms = new ArrayList<AUIElement>();
	
	protected abstract IScriptingLanguageEngine createLangEngine() throws CompileException;

	@Override public HudInformation compile(ConfigInfo info, String text, String filename) throws CompileException {
		if (mc.player==null) return HudInformation.of("");
		RuntimeCache rtcache = cache.get(text);
		IScriptingLanguageEngine wrapper = null;
		try {
			if (rtcache!=null&&rtcache.exception!=null) throw rtcache.exception;
			wrapper = rtcache==null?null:rtcache.engine;
			if (wrapper==null) {
				wrapper = createLangEngine();
				loadFunctions(wrapper);
				
				Exception exception = null;
				try {
					wrapper.evaluateCode(text, filename);
				} catch (Exception e) {
					exception = e;
				}
				cache.put(text, new RuntimeCache(wrapper,exception));
			}
			String TL = String.valueOf(wrapper.callFunctionSafe("topleft", ""));
			String BL = String.valueOf(wrapper.callFunctionSafe("bottomleft", ""));
			String TR = String.valueOf(wrapper.callFunctionSafe("topright", ""));
			String BR = String.valueOf(wrapper.callFunctionSafe("bottomright", ""));
			
			wrapper.callFunctionSafe("createElements", null);
			
			/* Scale */
			
			float TLscale = wrapper.readVariableSafe("topleftscale",1f).asFloat();
			float BLscale = wrapper.readVariableSafe("bottomleftscale",1f).asFloat();
			float TRscale = wrapper.readVariableSafe("toprightscale",1f).asFloat();
			float BRscale = wrapper.readVariableSafe("bottomrightscale",1f).asFloat();
			
			return new HudInformation(TL, TLscale, BL, BLscale, TR, TRscale, BR, BRscale,
					elms.toArray(new AUIElement[elms.size()]));
		} catch (CompileException e) {
			throw e;
		} catch (Exception e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			if (wrapper!=null) {
				throw wrapper.processException(e);
			} 
			if (e instanceof RuntimeException ex) throw ex;
			throw new CompileException(e.getMessage(),-1,-1,e);
		}
	}
	
	@Override public Object getVariable(String key) throws CompileException {
		Object obj = NumberData.getNumber(key);
		if ( obj!=null) return obj;
		if ((obj=StringData.getString (key))!=null) return obj;
		if ((obj=BooleanData.getBoolean(key))!=null) return obj;
		if ((obj=get(key))!=null) return obj;
		if ((obj=Hudder.config.globalVariables.get(key))!=null) return obj;
		return null;
	}
	public void loadFunctions(IScriptingLanguageEngine engine) {
		
		//Getters
		
		engine.bindFunction(s->getVariable(s[0].asString()), "get", "getVal", "getVariable");
		engine.bindFunction(s->NumberData.getNumber(s[0].asString()), "getNumber" );
		engine.bindFunction(s->StringData.getString(s[0].asString()), "getString" );
		engine.bindFunction(s->BooleanData.getBoolean(s[0].asString()), "getBoolean");
		
		engine.bindFunction(s->new TranslatedItemStack(mc.player.getInventory().getStack(s[0].asInt())), "getItem");
		
		//Setters
		
		engine.bindConsumer(s->put(s[0].asString(), s[1]), "set", "setVal", "setVariable");
		
		//ItemStacks
		
		//Item
		engine.bindConsumer(s->elms.add(new ItemElement(s[1].asInt(), s[2].asInt(),new ItemStack(Registries.ITEM.get(
				Identifier.tryParse(s[0].asString()))),s[3].asFloat(), false)),"drawItem");
		//Slot
		engine.bindConsumer(s->elms.add(new ItemElement(s[1].asInt(),s[2].asInt(),mc.player.getInventory()
				.getStack(s[0].asInt()),s[3].asFloat(), s[4].asBoolean())),"drawSlot");
		//Armor
		engine.bindConsumer(s->elms.add(new ItemElement(s[1].asInt(),s[2].asInt(),mc.player.getInventory()
				.getArmorStack(s[0].asInt()),s[3].asFloat(), s[4].asBoolean())),"drawArmor");
		//Offhand
		engine.bindConsumer(s->elms.add(new ItemElement(s[1].asInt(),s[2].asInt(),mc.player.getInventory()
				.offHand.get(0),s[3].asFloat(), s[4].asBoolean())),"drawOffhand");
		
		//Text
		
		engine.bindFunction(s->mc.textRenderer.getWidth(s[0].asString()), "strWidth");
		engine.bindConsumer(s-> {
			float scale = s[3].asFloat();
			int color = s[4].asInt();
			boolean shadow = s[5].asBoolean();
			boolean bg = s[6].asBoolean();
			int bgc = s[7].asInt();
			elms.add(new TextElement(s[0].asInt(),s[1].asInt(),s[2].asString(),scale,color,shadow,bg,bgc));
		}, "drawText");
		
		//Compile
		
		engine.bindFunction(s-> {
			try {
				ATextCompiler ecompiler = s.length>1?Compilers.getCompilerFromName(s[1].asString()):this;
				for (var i : HudCompilationManager.precomplistners) i.accept(ecompiler);
				HudInformation result = ecompiler.compile(Hudder.config,HudFileUtils.getFile(s[0].asString()),s[0].asString());
				Collections.addAll(elms, result.elements);
				for (var i : HudCompilationManager.postcomplistners) i.accept(ecompiler);
				return result;
			} catch (ReflectiveOperationException | IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Unknown compiler");
			}
		}, "compile", "run", "execute");
		
		//Texture

		engine.bindConsumer( s-> elms.add(new TextureElement(Identifier.tryParse(s[0].asString().trim()),s[1].asInt(),
				s[2].asInt(), s[3].asInt(),s[4].asInt())), "drawTexture");
		
		engine.bindFunction(s-> {
			try {
				boolean ex = HudFileUtils.exists(s[0].asString());
				if (!ex) return false;
				Identifier id = Identifier.of(s[0].asString().trim().toLowerCase());
				HudFileUtils.getAndRegisterImage(s[0].asString(),id);
				elms.add(new TextureElement(id,s[1].asInt(),s[2].asInt(),s[3].asInt(),s[4].asInt()));
			} catch (IOException e) {return false;}
			return true;
		}, "drawLocalTexture", "drawPNG", "drawImage", "image", "png");
		
		//Hotbar

		engine.bindConsumer(s->elms.add(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.STATUS_BARS)),
				"drawStatusBars", "statusbars");
		engine.bindConsumer(s->elms.add(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.EXP_AND_MOUNT_BAR)),
				"drawExpAndMountBars", "xpbar");
		engine.bindConsumer(s->elms.add(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.HOTBAR)),
				"drawHotbar", "hotbar");
		engine.bindConsumer(s->elms.add(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.ITEM_TOOLTIP)),
				"drawItemTooltip", "helditemtooltip");
		
		//Vertex

		engine.bindConsumer(s-> {
			try {
				elms.add(new TextureVerticesElement(s[0].asString(),s[1].asFloatArray(),s[2].asFloatArray()));
			} catch (IOException e) {
				throw new CompileException(e.getMessage(), 0, 0, e);
			}
		}, "textureVertices");
		
		engine.bindConsumer(s->elms.add(new ColorVerticesElement(s[0].asFloatArray(),s[1].asLong())),"colorvertices");
		
		//Misc
		
		engine.bindFunction(s->HudFileUtils.exists(s[0].asString()),"exists");
	}
	
	
	public static class TranslatedItemStack {
		public String name;
		public int count;
		public int maxcount;
		public int durability;
		public int maxdurability;
		public TranslatedItemStack(ItemStack stack) {
			name = stack.getItemName().getString();
			count = stack.getCount();
			maxcount = stack.getMaxCount();
			durability = stack.getMaxDamage()-stack.getDamage();
			maxdurability = stack.getMaxDamage();
		}
	}

	/**
	 * Saves the engine as well as any compiler exception that was thrown during compiliation.
	 */
	public static class RuntimeCache implements Closeable {
		public IScriptingLanguageEngine engine;
		public Exception exception;
		public RuntimeCache(IScriptingLanguageEngine engine, Exception exception) {
			this.engine=engine;
			this.exception=exception;
		}
		@Override public void close() throws IOException {
			exception = null;
			engine.close();
		}
	}
	
}
