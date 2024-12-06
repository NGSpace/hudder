package io.github.ngspace.hudder.compilers;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.ArrayElementManager;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.HudInformation;
import io.github.ngspace.hudder.compilers.utils.IScriptingLanguageEngine;
import io.github.ngspace.hudder.config.HudderConfig;
import io.github.ngspace.hudder.data_management.BooleanData;
import io.github.ngspace.hudder.data_management.NumberData;
import io.github.ngspace.hudder.data_management.ObjectData;
import io.github.ngspace.hudder.data_management.StringData;
import io.github.ngspace.hudder.hudder.HudCompilationManager;
import io.github.ngspace.hudder.uielements.AUIElement;
import io.github.ngspace.hudder.uielements.ItemElement;
import io.github.ngspace.hudder.utils.HudFileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class AScriptingLanguageCompiler extends AVarTextCompiler {
	
	protected static Minecraft mc = Minecraft.getInstance();
	
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
	public ArrayElementManager elms = new ArrayElementManager();
	
	protected abstract IScriptingLanguageEngine createLangEngine() throws CompileException;

	@Override public HudInformation compile(HudderConfig info, String text, String filename) throws CompileException {
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
					wrapper.close();
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
		if ((obj=ObjectData.getObject(key))!=null) return obj;
		if ((obj=get(key))!=null) return obj;
		if ((obj=Hudder.config.globalVariables.get(key))!=null) return obj;
		return null;
	}
	public void loadFunctions(IScriptingLanguageEngine engine) {
		
		//Item
		
		engine.bindConsumer(s->elms.add(new ItemElement(s[1].asInt(), s[2].asInt(),new ItemStack(BuiltInRegistries.ITEM.getValue(
				ResourceLocation.tryParse(s[0].asString()))),s[3].asFloat(), false)),"drawItem", "item");
		
		//Slot
		
		engine.bindConsumer(s->elms.add(new ItemElement(s[1].asInt(),s[2].asInt(),mc.player.getInventory()
				.getItem(s[0].asInt()),s[3].asFloat(), s[4].asBoolean())),"drawSlot", "slot");
		
		//Armor
		
		engine.bindConsumer(s->elms.add(new ItemElement(s[1].asInt(),s[2].asInt(),mc.player.getInventory()
				.getArmor(s[0].asInt()),s[3].asFloat(), s[4].asBoolean())),"drawArmor", "armor");
		
		//Offhand
		
		engine.bindConsumer(s->elms.add(new ItemElement(s[1].asInt(),s[2].asInt(),mc.player.getInventory()
				.offhand.get(0),s[3].asFloat(), s[4].asBoolean())),"drawOffhand", "offhand");
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
