package io.github.ngspace.hudder.compilers;

import static io.github.ngspace.hudder.Hudder.ins;
import static io.github.ngspace.hudder.data_management.BooleanData.getBoolean;
import static io.github.ngspace.hudder.data_management.NumberData.getNumber;
import static io.github.ngspace.hudder.data_management.StringData.getString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable.NoThisAndNoResult;
import com.caoccao.javet.interop.callback.IJavetDirectCallable.NoThisAndResult;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueGlobalObject;
import com.caoccao.javet.values.reference.V8ValueObject;

import io.github.ngspace.hudder.Hudder;
import io.github.ngspace.hudder.config.ConfigInfo;
import io.github.ngspace.hudder.config.ConfigManager;
import io.github.ngspace.hudder.meta.Element;
import io.github.ngspace.hudder.meta.ItemElement;
import io.github.ngspace.hudder.meta.TextElement;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class JavaScriptCompiler extends AVarTextCompiler {
	public JavaScriptCompiler() {Hudder.addPreCompilerListener(t ->{if (t==this) elms.clear();});}
	@Override public Object getVariable(String key) throws CompileException {
		Object obj = getNumber(key);
		if ( obj!=null) return obj;
		if ((obj=getString (key))!=null) return obj;
		if ((obj=getBoolean(key))!=null) return obj;
		if ((obj=get       (key))!=null) return obj;
		if ((obj=ConfigManager.getConfig().globalVariables.get(key))!=null) return obj;
		return null;
	}
	public static class RuntimeCache {
		public V8Runtime runtime;
		public Exception exception;

		public RuntimeCache(V8Runtime runtime, Exception exception) {this.runtime=runtime;this.exception=exception;}
	}
	public Element item(String id, int x, int y, double scale) {
		return new ItemElement(x, y, new ItemStack(Registries.ITEM.get(Identifier.tryParse(id))),(float)scale, false);
	}
	public Element slot(int s, int x, int y, double scale, boolean showcount) {
		return new ItemElement(x,y,ins.player.getInventory().getStack(s),(float) scale, showcount);
	}
	public Element armor(int s, int x, int y, double scale, boolean showcount) {
		return new ItemElement(x,y,ins.player.getInventory().getArmorStack(s),(float) scale, showcount);
	}
	public Element offhand(int x, int y, double scale, boolean showcount) {
		return new ItemElement(x,y,ins.player.getInventory().offHand.get(0),(float) scale, showcount);
	}
	public Map<String, RuntimeCache> cache = new HashMap<String, RuntimeCache>();
	public JavetProxyConverter OBJECT_CONVERTER = new JavetProxyConverter() {
		@SuppressWarnings("unchecked")
		@Override
		protected <T extends V8Value> T toV8Value(V8Runtime engine, Object object, int depth)
				throws JavetException {
			if (object instanceof CompileResult cr) {
				V8ValueObject newobj = engine.createV8ValueObject();
				newobj.set("topleft", cr.TopLeftText,
						"bottomleft", cr.BottomLeftText,
						"topright", cr.TopRightText,
						"bottomright", cr.BottomRightText,
						"topleftscale", cr.TLScale,
						"bottomleftscale", cr.BLScale,
						"toprightscale", cr.TRScale,
						"bottomrightscale", cr.BRScale);
				return (T) newobj;
			}
			return super.toV8Value(engine, object, depth);
		}
	};
	public V8Value asJSValue(V8Runtime engine, Object object) throws JavetException {
		return OBJECT_CONVERTER.toV8Value(engine, object);
	}
	public void bindFunction(V8ValueGlobalObject obj, String name, NoThisAndResult<Exception> func)
			throws JavetException {
		obj.bindFunction(new JavetCallbackContext(name, JavetCallbackType.DirectCallNoThisAndResult, func));
	}
	public void bindConsumer(V8ValueGlobalObject obj, String name, NoThisAndNoResult<Exception> consumer)
			throws JavetException {
		obj.bindFunction(new JavetCallbackContext(name, JavetCallbackType.DirectCallNoThisAndNoResult, consumer));
	}
	ArrayList<Element> elms = new ArrayList<Element>();
	@Override
	@SuppressWarnings("resource")
	public CompileResult compile(ConfigInfo info, String text) throws CompileException {
    	if (ins.player==null) return CompileResult.of("");
    	if (!info.javascript) throw new CompileException("JavaScript is disabled!",-1,-1);
    	RuntimeCache rtcache = cache.get(text);
	    try {
	    	
	    	if (rtcache!=null&&rtcache.exception!=null) throw rtcache.exception;
			V8Runtime v8 = rtcache==null?null:rtcache.runtime;
	    	V8ValueGlobalObject gb;
	    	if (v8==null) {
	    		v8 = V8Host.getV8Instance().createV8Runtime();
	    		gb = v8.getGlobalObject();
				v8.setConverter(OBJECT_CONVERTER);
				v8.getGlobalObject().set("console", JavaScriptIO.class);
				v8.getGlobalObject().set("hudder", JavaScriptIO.class);
	    		V8Runtime engine = v8;
	    		
	    		//System
	    		
	    		bindConsumer(gb, "log", s->JavaScriptIO.log(s[0].asString()));
	    		bindConsumer(gb, "warn", s->JavaScriptIO.warn(s[0].asString()));
	    		bindConsumer(gb, "error", s->JavaScriptIO.error(s[0].asString()));
	    		bindConsumer(gb, "alert", s->JavaScriptIO.alert(Text.of(s[0].asString())));
	    		
	    		//Getters
	    		
	        	NoThisAndResult<Exception> getval = s->asJSValue(engine,getVariable(s[0].asString()));
	        	bindFunction(gb, "get", getval);
	        	bindFunction(gb, "getVal", getval);
	        	bindFunction(gb, "getVariable", getval);
	        	bindFunction(gb, "getNumber", s->asJSValue(engine,getNumber(s[0].asString())));
	        	bindFunction(gb, "getString", s->asJSValue(engine,getString(s[0].asString())));
	        	bindFunction(gb, "getBoolean", s->asJSValue(engine,getBoolean(s[0].asString())));
	        	
	        	//Setters
	        	
	        	NoThisAndNoResult<Exception> setval = s-> {
	        		try {put(s[0].asString(), s[1].asString());} catch (Exception e) {/**/}
	        	};
	        	bindConsumer(gb, "setVal", setval);
	        	bindConsumer(gb, "set", setval);
	        	bindConsumer(gb, "put", setval);
	        	bindConsumer(gb, "setVariable", setval);
	        	
	        	//ItemStacks

	        	bindConsumer(gb, "drawItem", s->
        			elms.add(item(s[0].asString(), s[1].asInt(), s[2].asInt(), s[3].asDouble()))
	        	);
	        	bindConsumer(gb, "drawSlot", s-> {
        			boolean showcount;try {showcount = s[4].asBoolean();} catch (Exception e) {showcount = true;}
        			elms.add(slot(s[0].asInt(), s[1].asInt(), s[2].asInt(), s[3].asDouble(), showcount));
	        	});
	        	bindConsumer(gb, "drawArmor", s-> {
        			boolean showcount;try {showcount = s[4].asBoolean();} catch (Exception e) {showcount = true;}
        			elms.add(armor(s[0].asInt(), s[1].asInt(), s[2].asInt(), s[3].asDouble(), showcount));
	        	});
	        	bindConsumer(gb, "drawOffhand", s-> {
        			boolean showcount;try {showcount = s[4].asBoolean();} catch (Exception e) {showcount = true;}
        			elms.add(offhand(s[1].asInt(), s[2].asInt(), s[3].asDouble(), showcount));
	        	});
	        	
	        	//Text
	        	
	        	bindFunction(gb, "strWidth", s->asJSValue(engine,Hudder.ins.textRenderer.getWidth(s[0].asString())));
	        	bindConsumer(gb, "drawText", s-> {
	        		float scale = s.length>3 ? (float)s[3].asDouble() : info.scale;
	        		int color = s.length>4 ? s[4].asInt() : info.color;
	        		boolean shadow = s.length>5 ? s[5].asBoolean() : info.shadow;
	        		elms.add(new TextElement(s[0].asInt(),s[1].asInt(),s[2].asString(),scale,color,shadow));
	        	});
	        	
	        	//Compile
	        	
	        	NoThisAndResult<Exception> compile = s-> {
	    			ATextCompiler compiler = s.length>1?info.getCompilerFromName(s[1].asString()):this;
	    			CompileResult res = compiler.compile(info, info.getFile(s[0].asString()));
	    			Collections.addAll(elms, res.elements);
	    			return asJSValue(engine, res);
	        	};
	        	bindFunction(gb, "compile", compile);
	        	
	        	Exception exception = null;
	        	try {
		        	v8.getExecutor(text).executeVoid();
				} catch (Exception e) {exception = e;} //Do not recompile if there is a compiler error.
//	        	if (cache.size()>0) for (var v : cache.values()) v.runtime.close(true);
//	        	cache.clear();
	        	cache.put(text, new RuntimeCache(v8,exception));
	    	}
    		gb = v8.getGlobalObject();
    		
    		
	    	String TL = gb.has("topleft"    )?((V8ValueFunction)gb.get("topleft"    )).callString(null):"";
	    	String BL = gb.has("bottomleft" )?((V8ValueFunction)gb.get("bottomleft" )).callString(null):"";
	    	String TR = gb.has("topright"   )?((V8ValueFunction)gb.get("topright"   )).callString(null):"";
	    	String BR = gb.has("bottomright")?((V8ValueFunction)gb.get("bottomright")).callString(null):"";
	    	
	    	if (gb.has("createElements")) ((V8ValueFunction)gb.get("createElements")).call(null);
	    	
	    	/* Scale */
	    	
	    	float TLscale = gb.has("topleftscale"    )?TLscale = (float) gb.get("topleftscale"    ).asDouble():1;
	    	float BLscale = gb.has("bottomleftscale" )?TLscale = (float) gb.get("bottomleftscale" ).asDouble():1;
	    	float TRscale = gb.has("toprightscale"   )?TLscale = (float) gb.get("toprightscale"   ).asDouble():1;
	    	float BRscale = gb.has("bottomrightscale")?TLscale = (float) gb.get("bottomrightscale").asDouble():1;
	    	
		    return new CompileResult(TL, TLscale, BL, BLscale, TR, TRscale, BR, BRscale,
		    		elms.toArray(new Element[elms.size()])); 
		} catch (Exception e) {
			if (Hudder.IS_DEBUG) e.printStackTrace();
			throw new CompileException(e.getLocalizedMessage(),-1,-1);
		}
	}
	public static class JavaScriptIO {private JavaScriptIO() {}
		public static void log  (Object str) {Hudder.log  (str);}
		public static void warn (Object str) {Hudder.warn (str);}
		public static void error(Object str) {Hudder.error(str);}
		public static void debug(Object str) {Hudder.debug(str);}
		public static void alert(Object str) {Hudder.alert(str);}
		public static void showToast(String title, String content) {
			Hudder.showToast(Hudder.ins,Text.literal(title).formatted(Formatting.BOLD), Text.literal(content));
		}
		public static void exception(String message) {throw new RuntimeException(message);}
	}
}