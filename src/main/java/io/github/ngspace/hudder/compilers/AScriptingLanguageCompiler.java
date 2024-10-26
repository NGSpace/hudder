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
import io.github.ngspace.hudder.methods.elements.GameHudElement;
import io.github.ngspace.hudder.methods.elements.GameHudElement.GuiType;
import io.github.ngspace.hudder.methods.elements.ItemElement;
import io.github.ngspace.hudder.methods.elements.TextElement;
import io.github.ngspace.hudder.methods.elements.TextureElement;
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
	    	
	    	float TLscale = ((Number) wrapper.readVariableSafe("topleftscale",1f)).floatValue();
	    	float BLscale = ((Number) wrapper.readVariableSafe("bottomleftscale",1f)).floatValue();
	    	float TRscale = ((Number) wrapper.readVariableSafe("toprightscale",1f)).floatValue();
	    	float BRscale = ((Number) wrapper.readVariableSafe("bottomrightscale",1f)).floatValue();
	    	
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
		
    	engine.bindFunction(s->getVariable(((String)s[0])), "get", "getVal", "getVariable");
    	engine.bindFunction(s->NumberData.getNumber  (((String)s[0])), "getNumber" );
    	engine.bindFunction(s->StringData.getString  (((String)s[0])), "getString" );
    	engine.bindFunction(s->BooleanData.getBoolean (((String)s[0])), "getBoolean");
    	
    	engine.bindFunction(s->new TranslatedItemStack(mc.player.getInventory().getStack(((Number)s[0]).intValue())), "getItem");
    	
    	//Setters
    	
    	engine.bindConsumer( s->put((String)s[0], s[1]), "set", "setVal", "setVariable");
    	
    	//ItemStacks
    	
    	//Item
    	engine.bindConsumer(s->elms.add(new ItemElement(((Number)s[1]).intValue(), ((Number)s[2]).intValue(),new ItemStack(Registries.ITEM.get(
    			Identifier.tryParse(((String)s[0])))),((Number)s[3]).floatValue(), false)),"drawItem");
    	//Slot
    	engine.bindConsumer(s->elms.add(new ItemElement(((Number)s[1]).intValue(),((Number)s[2]).intValue(),mc.player.getInventory()
    			.getStack(((Number)s[0]).intValue()),((Number)s[3]).floatValue(), (boolean)s[4])),"drawSlot");
    	//Armor
    	engine.bindConsumer(s->elms.add(new ItemElement(((Number)s[1]).intValue(),((Number)s[2]).intValue(),mc.player.getInventory()
    			.getArmorStack(((Number)s[0]).intValue()),((Number)s[3]).floatValue(), (boolean)s[4])),"drawArmor");
    	//Offhand
    	engine.bindConsumer(s->elms.add(new ItemElement(((Number)s[1]).intValue(),((Number)s[2]).intValue(),mc.player.getInventory()
    			.offHand.get(0),((Number)s[3]).floatValue(), (boolean)s[4])),"drawOffhand");
    	
    	//Text
    	
    	engine.bindFunction(s->mc.textRenderer.getWidth(((String)s[0])), "strWidth");
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
				for (var i : HudCompilationManager.precomplistners) i.accept(ecompiler);
				HudInformation result = ecompiler.compile(Hudder.config,HudFileUtils.getFile((String)s[0]),(String)s[0]);
				Collections.addAll(elms, result.elements);
				for (var i : HudCompilationManager.postcomplistners) i.accept(ecompiler);
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
