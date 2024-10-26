package io.github.ngspace.hudder.compilers;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.JavaScriptEngineWrapper.JavaScriptIO;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.data_management.BooleanData;
import io.github.ngspace.hudder.data_management.NumberData;
import io.github.ngspace.hudder.data_management.StringData;
import io.github.ngspace.hudder.methods.elements.AUIElement;
import io.github.ngspace.hudder.methods.elements.GameHudElement;
import io.github.ngspace.hudder.methods.elements.GameHudElement.GuiType;
import io.github.ngspace.hudder.methods.elements.ItemElement;
import io.github.ngspace.hudder.methods.elements.TextElement;
import io.github.ngspace.hudder.methods.elements.TextureElement;
import io.github.ngspace.hudder.util.HudFileUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;


public class JavaScriptCompiler extends AVarTextCompiler {
	
	public Map<String, RuntimeCache> cache = new HashMap<String, RuntimeCache>();
	public List<AUIElement> elms = new ArrayList<AUIElement>();
	
	public JavaScriptCompiler() {
		Hudder.addPreCompilerListener(c->{if(c==this) elms.clear();});
		HudFileUtils.addClearCacheListener(()->{
			try {
				for(RuntimeCache c:cache.values())c.close();
			} catch (IOException e) {
				if (Hudder.IS_DEBUG) e.printStackTrace();
				throw new CompileException(e.getLocalizedMessage());
			}
			cache.clear();
		});
	}
	@Override public Object getVariable(String key) throws CompileException {
		Object obj = NumberData.getNumber(key);
		if ( obj!=null) return obj;
		if ((obj=StringData.getString (key))!=null) return obj;
		if ((obj=BooleanData.getBoolean(key))!=null) return obj;
		if ((obj=get       (key))!=null) return obj;
		if ((obj=Hudder.config.globalVariables.get(key))!=null) return obj;
		return null;
	}
	
	@Override
	public CompileResult compile(ConfigInfo info, String text) throws CompileException {
    	if (!info.javascript) throw new CompileException("JavaScript is disabled!",-1,-1);
    	if (Hudder.ins.player==null) return CompileResult.of("");
    	RuntimeCache rtcache = cache.get(text);
    	JavaScriptEngineWrapper wrapper = null;
	    try {
	    	if (rtcache!=null&&rtcache.exception!=null) throw rtcache.exception;
			wrapper = rtcache==null?null:rtcache.engine;
	    	if (wrapper==null) {
	    		wrapper = new JavaScriptEngineWrapper();
	    		loadFunctions(wrapper);
	    		
	        	Exception exception = null;
	        	try {
	        		wrapper.evaluateString(text, "hud.js");
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
	    	
	    	float TLscale = ((Number) wrapper.readVariableSafe("topleftscale",1f)).floatValue();
	    	float BLscale = ((Number) wrapper.readVariableSafe("bottomleftscale",1f)).floatValue();
	    	float TRscale = ((Number) wrapper.readVariableSafe("toprightscale",1f)).floatValue();
	    	float BRscale = ((Number) wrapper.readVariableSafe("bottomrightscale",1f)).floatValue();
	    	
		    return new CompileResult(TL, TLscale, BL, BLscale, TR, TRscale, BR, BRscale,
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
	public void loadFunctions(JavaScriptEngineWrapper engine) {
		//System
		var JavaScriptIO = new JavaScriptIO();
		
		engine.bindConsumer( s->JavaScriptIO.log(s[0]), "log");
		engine.bindConsumer( s->JavaScriptIO.warn(s[0]), "warn");
		engine.bindConsumer( s->JavaScriptIO.error(s[0]), "error");
		engine.bindConsumer( s->JavaScriptIO.alert(s[0]), "alert");
		
		//Getters
		
    	engine.bindFunction(s->getVariable(((String)s[0])), "get", "getVal", "getVariable");
    	engine.bindFunction(s->NumberData.getNumber  (((String)s[0])), "getNumber" );
    	engine.bindFunction(s->StringData.getString  (((String)s[0])), "getString" );
    	engine.bindFunction(s->BooleanData.getBoolean (((String)s[0])), "getBoolean");
    	
    	engine.bindFunction(s->translate(Hudder.ins.player.getInventory().getStack(((Number)s[0]).intValue())), "getItem");
    	
    	//Setters
    	
    	engine.bindConsumer( s->put((String)s[0], s[1]), "set", "setVal", "setVariable");
    	
    	//ItemStacks
    	
    	//Item
    	engine.bindConsumer(s->elms.add(new ItemElement(((Number)s[1]).intValue(), ((Number)s[2]).intValue(), new ItemStack(Registries.ITEM.get(
    			Identifier.tryParse(((String)s[0])))),((Number)s[3]).floatValue(), false)),"drawItem");
    	//Slot
    	engine.bindConsumer(s->elms.add(new ItemElement(((Number)s[1]).intValue(),((Number)s[2]).intValue(),Hudder.ins.player.getInventory()
    			.getStack(((Number)s[0]).intValue()),((Number)s[3]).floatValue(), (boolean)s[4])),"drawSlot");
    	//Armor
    	engine.bindConsumer(s->elms.add(new ItemElement(((Number)s[1]).intValue(),((Number)s[2]).intValue(),Hudder.ins.player.getInventory()
    			.getArmorStack(((Number)s[0]).intValue()),((Number)s[3]).floatValue(), (boolean)s[4])),"drawArmor");
    	//Offhand
    	engine.bindConsumer(s->elms.add(new ItemElement(((Number)s[1]).intValue(),((Number)s[2]).intValue(),Hudder.ins.player.getInventory()
    			.offHand.get(0),((Number)s[3]).floatValue(), (boolean)s[4])),"drawOffhand");
    	
    	//Text
    	
    	engine.bindFunction(s->Hudder.ins.textRenderer.getWidth(((String)s[0])), "strWidth");
    	engine.bindConsumer(s-> {
    		float scale = ((Number)s[3]).floatValue();
    		int color = ((Number)s[4]).intValue();
    		boolean shadow = (boolean)s[5];
    		boolean bg = (boolean)s[6];
    		int bgc = ((Number)s[7]).intValue();
    		elms.add(new TextElement(((Number)s[0]).intValue(),((Number)s[1]).intValue(),((String)s[2]),scale,color,shadow,bg,bgc));
    	}, "drawText");
    	
    	//Compile
    	
    	engine.bindFunction(s-> {
			try {
				ATextCompiler ecompiler = s.length>1?Compilers.getCompilerFromName(((String)s[1])):this;
				for (var i : Hudder.precomplistners) i.accept(ecompiler);
				CompileResult result = ecompiler.compile(Hudder.config, HudFileUtils.getFile(((String)s[0])));
				Collections.addAll(elms, result.elements);
				for (var i : Hudder.postcomplistners) i.accept(ecompiler);
				return result;
			} catch (ReflectiveOperationException | IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Unknown compiler");
			}
    	}, "compile", "run", "execute");
    	
    	//Texture

    	engine.bindConsumer( s-> elms.add(new TextureElement(Identifier.tryParse(((String)s[0]).trim()),((Number)s[1]).intValue(),
    			((Number)s[2]).intValue(), ((Number)s[3]).intValue(),((Number)s[4]).intValue())), "drawTexture");
    	
		engine.bindFunction(s-> {
    		try {
    			boolean ex = HudFileUtils.exists((String)s[0]);
    			if (!ex) return false;
        		Identifier id = Identifier.of(((String)s[0]).trim().toLowerCase());
        		HudFileUtils.getAndRegisterImage(((String)s[0]),id);
        		elms.add(new TextureElement(id,((Number)s[1]).intValue(),((Number)s[2]).intValue(),((Number)s[3]).intValue(),((Number)s[4]).intValue()));
			} catch (IOException e) {return false;}
    		return true;
    	}, "drawLocalTexture", "drawPNG", "drawImage");
    	
    	//Hotbar

    	engine.bindConsumer(s->elms.add(new GameHudElement(((Number)s[0]).intValue(),((Number)s[1]).intValue(),GuiType.STATUS_BARS)),
    			"drawStatusBars");
    	engine.bindConsumer(s->elms.add(new GameHudElement(((Number)s[0]).intValue(),((Number)s[1]).intValue(),GuiType.EXP_AND_MOUNT_BAR)),
    			"drawExpAndMountBars");
    	engine.bindConsumer(s->elms.add(new GameHudElement(((Number)s[0]).intValue(),((Number)s[1]).intValue(),GuiType.HOTBAR)),
    			"drawHotbar");
    	engine.bindConsumer(s->elms.add(new GameHudElement(((Number)s[0]).intValue(),((Number)s[1]).intValue(),GuiType.ITEM_TOOLTIP)),
    			"drawItemTooltip");
    	
    	
    	engine.bindFunction(s->HudFileUtils.exists(((String)s[0])),"exists");
	}

	private Object translate(ItemStack stack) {
		return new TranslatedItemStack(stack);
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
		public JavaScriptEngineWrapper engine;
		public Exception exception;
		public RuntimeCache(JavaScriptEngineWrapper engine, Exception exception) {
			this.engine=engine;
			this.exception=exception;
		}
		@Override public void close() throws IOException {
			exception = null;
			engine.close();
		}
	}
}