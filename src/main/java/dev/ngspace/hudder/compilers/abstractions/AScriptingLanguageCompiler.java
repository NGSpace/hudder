package dev.ngspace.hudder.compilers.abstractions;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.compilers.utils.CompileException;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.compilers.utils.functionandconsumerapi.ArrayElementManager;
import dev.ngspace.hudder.data_management.ObjectDataAPI;
import dev.ngspace.hudder.data_management.api.DataVariableRegistry;
import dev.ngspace.hudder.main.HudCompilationManager;
import dev.ngspace.hudder.main.config.HudderConfig;
import dev.ngspace.hudder.uielements.AUIElement;
import dev.ngspace.hudder.uielements.ItemElement;
import dev.ngspace.hudder.utils.HudFileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public abstract class AScriptingLanguageCompiler extends AVarTextCompiler {
	
	protected static Minecraft mc = Minecraft.getInstance();
	
	public Map<String, RuntimeCache> cache = new HashMap<String, RuntimeCache>();
	public ArrayElementManager elms = new ArrayElementManager();
	
	protected AScriptingLanguageCompiler() {
		HudCompilationManager.addPreCompilerListener(c->{if(c==this) elms.clear();});
		HudFileUtils.addReloadResourcesListener(()->{
			for(RuntimeCache c:cache.values()) c.close();
			cache.clear();
		});
	}
	
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

	@SuppressWarnings("removal")
	@Override public Object getVariable(String key) throws CompileException {
		Object obj = DataVariableRegistry.getAny(key);
		if ( obj!=null) return obj;
		if ((obj=ObjectDataAPI.getObject(key))!=null) return obj;
		if ((obj=get(key))!=null) return obj;
		if ((obj=Hudder.config.globalVariables.get(key))!=null) return obj;
		return null;
	}
	public void loadFunctions(IScriptingLanguageEngine engine) {
		
		//Item
		
		engine.bindConsumer(s->elms.add(new ItemElement(s[1].asInt(), s[2].asInt(),new ItemStack(BuiltInRegistries.ITEM.getValue(
				Identifier.tryParse(s[0].asString()))),s[3].asFloat(), false)),"drawItem", "item");
		
		//Slot
		
		engine.bindConsumer(s->elms.add(new ItemElement(s[1].asInt(),s[2].asInt(),mc.player.getInventory()
				.getItem(s[0].asInt()),s[3].asFloat(), s[4].asBoolean())),"drawSlot", "slot");
		
		//Armor
		
		engine.bindConsumer(s->elms.add(new ItemElement(s[1].asInt(),s[2].asInt(),mc.player.getInventory()
				.getItem(36+s[0].asInt()),s[3].asFloat(), s[4].asBoolean())),"drawArmor", "armor");
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
