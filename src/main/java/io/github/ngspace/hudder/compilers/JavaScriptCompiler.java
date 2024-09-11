package io.github.ngspace.hudder.compilers;

import static com.caoccao.javet.interop.callback.JavetCallbackType.DirectCallNoThisAndNoResult;
import static com.caoccao.javet.interop.callback.JavetCallbackType.DirectCallThisAndResult;
import static io.github.ngspace.hudder.Hudder.config;
import static io.github.ngspace.hudder.Hudder.ins;
import static io.github.ngspace.hudder.data_management.BooleanData.getBoolean;
import static io.github.ngspace.hudder.data_management.NumberData.getNumber;
import static io.github.ngspace.hudder.data_management.StringData.getString;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable.NoThisAndNoResult;
import com.caoccao.javet.interop.callback.IJavetDirectCallable.ThisAndResult;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueGlobalObject;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.compilers.utils.CompileException;
import io.github.ngspace.hudder.compilers.utils.CompileResult;
import io.github.ngspace.hudder.compilers.utils.Compilers;
import io.github.ngspace.hudder.compilers.utils.JavetObjConverter;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.methods.elements.AUIElement;
import io.github.ngspace.hudder.methods.elements.GameHudElement;
import io.github.ngspace.hudder.methods.elements.ItemElement;
import io.github.ngspace.hudder.methods.elements.TextElement;
import io.github.ngspace.hudder.methods.elements.TextureElement;
import io.github.ngspace.hudder.methods.elements.GameHudElement.GuiType;
import io.github.ngspace.hudder.util.HudFileUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class JavaScriptCompiler extends AVarTextCompiler {
	
	public Map<String, RuntimeCache> cache = new HashMap<String, RuntimeCache>();
	public static JavetProxyConverter OBJECT_CONVERTER = new JavetObjConverter();
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
		Object obj = getNumber(key);
		if ( obj!=null) return obj;
		if ((obj=getString (key))!=null) return obj;
		if ((obj=getBoolean(key))!=null) return obj;
		if ((obj=get       (key))!=null) return obj;
		if ((obj=config.globalVariables.get(key))!=null) return obj;
		return null;
	}
	public AUIElement slot(int s, int x, int y, double scale, boolean showcount) {
		return new ItemElement(x,y,ins.player.getInventory().getStack(s),(float) scale, showcount);
	}
	public AUIElement armor(int s, int x, int y, double scale, boolean showcount) {
		return new ItemElement(x,y,ins.player.getInventory().getArmorStack(s),(float) scale, showcount);
	}
	public AUIElement offhand(int x, int y, double scale, boolean showcount) {
		return new ItemElement(x,y,ins.player.getInventory().offHand.get(0),(float) scale, showcount);
	}
	
	public static V8Value asJSValue(V8Runtime engine, Object object) throws JavetException {
		return OBJECT_CONVERTER.toV8Value(engine, object);
	}
	
	@Override
	public CompileResult compile(ConfigInfo info, String text) throws CompileException {
    	if (!info.javascript) throw new CompileException("JavaScript is disabled!",-1,-1);
    	if (ins.player==null) return CompileResult.of("");
    	RuntimeCache rtcache = cache.get(text);
	    try {
	    	if (rtcache!=null&&rtcache.exception!=null) throw rtcache.exception;
			V8Runtime v8 = rtcache==null?null:rtcache.runtime;
	    	if (v8==null) {
	    		v8 = V8Host.getV8Instance().createV8Runtime();
				v8.setConverter(OBJECT_CONVERTER);
				v8.getGlobalObject().set("console", JavaScriptIO.class);
				v8.getGlobalObject().set("hudder" , JavaScriptIO.class);
	    		
	    		loadFunctions(v8);
	    		
	        	Exception exception = null;
	        	//Do not recompile if there is a compiler error.
	        	try {v8.getExecutor(text).executeVoid();} catch (Exception e) {exception = e;}
	        	cache.put(text, new RuntimeCache(v8,exception));
	    	}
	    	V8ValueGlobalObject gb = v8.getGlobalObject();
    		

	    	String TL = gb.has("topleft"    )?((V8ValueFunction)gb.get("topleft"    )).callString(null):"";
	    	String BL = gb.has("bottomleft" )?((V8ValueFunction)gb.get("bottomleft" )).callString(null):"";
	    	String TR = gb.has("topright"   )?((V8ValueFunction)gb.get("topright"   )).callString(null):"";
	    	String BR = gb.has("bottomright")?((V8ValueFunction)gb.get("bottomright")).callString(null):"";
	    	
	    	if (gb.has("createElements")) ((V8ValueFunction)gb.get("createElements")).call(null);
	    	
	    	/* Scale */
	    	
	    	float TLscale = gb.has("topleftscale"    ) ? (float) gb.get("topleftscale"    ).asDouble():1;
	    	float BLscale = gb.has("bottomleftscale" ) ? (float) gb.get("bottomleftscale" ).asDouble():1;
	    	float TRscale = gb.has("toprightscale"   ) ? (float) gb.get("toprightscale"   ).asDouble():1;
	    	float BRscale = gb.has("bottomrightscale") ? (float) gb.get("bottomrightscale").asDouble():1;
	    	
		    return new CompileResult(TL, TLscale, BL, BLscale, TR, TRscale, BR, BRscale,
		    		elms.toArray(new AUIElement[elms.size()])); 
		} catch (JavetExecutionException e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			var err = e.getScriptingError();
			String msg = "\u00A74"+err.getDetailedMessage().substring(7)
					+"\n\u00A7bat Line "+err.getLineNumber()+" at col "+err.getStartColumn();
			throw new CompileException(msg,-1,-1);
		} catch (JavetException e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			throw new CompileException(e.getError().getMessage(e.getParameters()),-1,-1);
		} catch (CompileException e) {throw e;} catch (Exception e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			throw new CompileException(e.getLocalizedMessage(),-1,-1);
		}
	}
	public void loadFunctions(V8Runtime engine) throws JavetException {
		V8ValueGlobalObject gb = engine.getGlobalObject();
		//System
		
		bindConsumer(gb, s->JavaScriptIO.log(s[0].asString()), "log");
		bindConsumer(gb, s->JavaScriptIO.warn(s[0].asString()), "warn");
		bindConsumer(gb, s->JavaScriptIO.error(s[0].asString()), "error");
		bindConsumer(gb, s->JavaScriptIO.alert(Text.of(s[0].asString())), "alert");
		
		//Getters
		
    	bindFunction(gb, s->getVariable(s[0].asString()), "get", "getVal", "getVariable");
    	bindFunction(gb, s->getNumber  (s[0].asString()), "getNumber" );
    	bindFunction(gb, s->getString  (s[0].asString()), "getString" );
    	bindFunction(gb, s->getBoolean (s[0].asString()), "getBoolean");
    	
    	bindFunction(gb, s->Hudder.ins.player.getInventory().getStack(s[0].asInt()), "getItem");
    	
    	//Setters
    	
    	Cons<Exception> setval = s->put(s[0].asString(), s[1].asString());
    	bindConsumer(gb, setval, "set", "setVal", "setVariable");
    	
    	//ItemStacks
    	
    	//Item
    	bindConsumer(gb,s->elms.add(new ItemElement(s[1].asInt(), s[2].asInt(), new ItemStack(Registries.ITEM.get(
    			Identifier.tryParse(s[0].asString()))),(float)s[3].asDouble(), false)),"drawItem");
    	//Slot
    	bindConsumer(gb,s->elms.add(new ItemElement(s[1].asInt(),s[2].asInt(),ins.player.getInventory()
    			.getStack(s[0].asInt()),(float) s[3].asDouble(), s[4].asBoolean())),"drawSlot");
    	//Armor
    	bindConsumer(gb,s->elms.add(new ItemElement(s[1].asInt(),s[2].asInt(),ins.player.getInventory()
    			.getArmorStack(s[0].asInt()),(float) s[3].asDouble(), s[4].asBoolean())),"drawArmor");
    	//Offhand
    	bindConsumer(gb,s->elms.add(new ItemElement(s[1].asInt(),s[2].asInt(),ins.player.getInventory()
    			.offHand.get(0),(float) s[3].asDouble(), s[4].asBoolean())),"drawOffhand");
    	
    	//Text
    	
    	bindFunction(gb, s->Hudder.ins.textRenderer.getWidth(s[0].asString()), "strWidth");
    	bindConsumer(gb, s-> {
    		float scale = s.length>3 ? (float)s[3].asDouble() : config.scale;
    		int color = s.length>4 ? s[4].asInt() : config.color;
    		boolean shadow = s.length>5 ? s[5].asBoolean() : config.shadow;
    		boolean bg = s.length>6 ? s[6].asBoolean() : config.background;
    		int bgc = s.length>7 ? s[7].asInt() : config.backgroundcolor;
    		elms.add(new TextElement(s[0].asInt(),s[1].asInt(),s[2].asString(),scale,color,shadow,bg,bgc));
    	}, "drawText");
    	
    	//Compile
    	
    	bindFunction(gb, s-> {
			CompileResult result = (s.length>1?Compilers.getCompilerFromName(s[1].asString()):this)
					.compile(config, HudFileUtils.getFile(s[0].asString()));
			Collections.addAll(elms, result.elements);
			return result;
    	}, "compile", "run", "execute");
    	
    	//Texture

    	bindConsumer(gb, s-> elms.add(new TextureElement(Identifier.tryParse(s[0].asString().trim()),s[1].asInt(),
    			s[2].asInt(), s[3].asInt(),s[4].asInt())), "drawTexture");
    	
    	bindFunction(gb, s-> {
    		try {
    			boolean ex = HudFileUtils.exists(HudFileUtils.FOLDER + s[0].asString());
    			if (!ex) return false;
        		Identifier id = Identifier.of(s[0].asString().trim().toLowerCase());
        		HudFileUtils.getAndRegisterImage(s[0].asString(),id);
        		elms.add(new TextureElement(id,s[1].asInt(),s[2].asInt(),s[3].asInt(),s[4].asInt()));
			} catch (IOException e) {return false;}
    		return true;
    	}, "drawLocalTexture", "drawPNG", "drawImage");
    	
    	//Hotbar

    	bindConsumer(gb,s->elms.add(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.STATUS_BARS)),
    			"drawStatusBars");
    	bindConsumer(gb,s->elms.add(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.EXP_AND_MOUNT_BAR)),
    			"drawExpAndMountBars");
    	bindConsumer(gb,s->elms.add(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.HOTBAR)),
    			"drawHotbar");
    	bindConsumer(gb,s->elms.add(new GameHudElement(s[0].asInt(),s[1].asInt(),GuiType.ITEM_TOOLTIP)),
    			"drawItemTooltip");
    	
    	
    	bindFunction(gb,s->HudFileUtils.exists(s[0].asString()),"exists");
	}
	
	/**
	 * Adds a function to the v8 engine.
	 * @param obj - the GlobalObject of the v8 runtime
	 * @param func - the function to be binded to the function
	 * @param names - the names the function will have
	 * @throws JavetException
	 */
	public void bindFunction(V8ValueGlobalObject obj, Func<Exception> func, String... names) throws JavetException {
		for (String name:names) obj.bindFunction(new JavetCallbackContext(name, DirectCallThisAndResult, func));
	}
	/**
	 * Adds a consumer to the v8 engine.
	 * @param obj - the GlobalObject of the v8 runtime
	 * @param cons - the consumer to be binded to the function
	 * @param names - the names the consumer will have
	 * @throws JavetException
	 */
	public void bindConsumer(V8ValueGlobalObject obj, Cons<Exception> cons, String... names) throws JavetException {
		for (String name:names) obj.bindFunction(new JavetCallbackContext(name, DirectCallNoThisAndNoResult, cons));
	}
	
	
	/**
	 * the {@code console} object in the runtime
	 */
	public static class JavaScriptIO {private JavaScriptIO() {}
		public static void log  (Object str) {Hudder.log  (str);}
		public static void warn (Object str) {Hudder.warn (str);}
		public static void error(Object str) {Hudder.error(str);}
		public static void debug(Object str) {Hudder.debug(str);}
		public static void alert(Object str) {Hudder.alert(str);}
		public static void showToast(String title, String content) {
			Hudder.showToast(Hudder.ins,Text.literal(title).formatted(Formatting.BOLD), Text.literal(content));
		}
	}
	/**
	 * Saves a precompiled Javet runtime as well as any compiler exception that was thrown during compiliation.
	 */
	public static class RuntimeCache implements Closeable {
		public V8Runtime runtime;
		public Exception exception;
		public RuntimeCache(V8Runtime runtime, Exception exception) {this.runtime=runtime;this.exception=exception;}
		@Override
		public void close() throws IOException {
			exception = null;
			try {
				runtime.close();
			} catch (JavetException e) {
				throw new IOException(e.getLocalizedMessage());
			}
		}
	}
	/**
	 * Takes in values and returns a value.
	 * Should be used alongside {@code bindFunction}
	 * @param <E> any exception this function might throw.
	 */
	@FunctionalInterface public static interface Func<E extends Exception> extends ThisAndResult<E> {
		public default V8Value call(V8Value thisObject, V8Value... v8Values) throws JavetException, E {
			return asJSValue(thisObject.getV8Runtime(),exec(v8Values));
		}
		public Object exec(V8Value... v8Values) throws JavetException, E;
	}
	/**
	 * Like Func<E> but it doesn't return a value
	 * Should be used alongside {@code bindConsumer}
	 * @param <E> any exception this consumer might throw.
	 */
	@FunctionalInterface public static interface Cons<E extends Exception> extends NoThisAndNoResult<E> {}
	@Override public void reset() {cache.clear();variables.clear();}
}