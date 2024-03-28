package io.github.ngspace.hudder.compilers;

//import static io.github.ngspace.hudder.Hudder.player;
//
//import java.util.LinkedList;
//import java.util.function.BiConsumer;
//import java.util.function.Function;
//
//import javax.script.ScriptContext;
//import javax.script.ScriptEngineManager;
//import javax.script.ScriptException;
//
//import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
//import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
//
//import io.github.ngspace.hudder.Hudder;
//import io.github.ngspace.hudder.config.ConfigInfo;
//import io.github.ngspace.hudder.meta.Element;
//import io.github.ngspace.hudder.meta.ItemElement;

public class JavaScriptCompiler {private JavaScriptCompiler() {}// extends AVarTextCompiler {
//	
//	public static final String PRESCRIPT = """
//var TLScale = 1;
//var BLScale = 1;
//var TRScale = 1;
//var BRScale = 1;
//
//function topleft() {
//    return "";
//}
//function bottomleft() {
//    return "";
//}
//function topright() {
//    return "";
//}
//function bottomright() {
//    return "";
//}
//function createelements() {}
//
//""";
//
//	@FunctionalInterface
//	interface Function5<A, B, C, D, E, R> {
//	    public R apply(A one, B two, C three, D four, E five);
//	}
//	@FunctionalInterface
//	interface Function2<A1, A2, R> {
//	    public R apply(A1 one, A2 two);
//	}
//	@FunctionalInterface
//	interface Consumer4<A, B, C, D> {
//	    public A accept(A one, B two, C three, D four);
//	}
//	
//	@Override public Object getVariable(String key) throws CompileException {
//		return getPureVariable(key);
//	}
//	
//	public int slotelement(int s, int x, int y, int scale) {
//		new Exception("slotElement").printStackTrace();
//		elms.add(new ItemElement(x,y,player.getInventory().getStack(s),scale, true));
//		return 0;
//	}
//	
//	LinkedList<Element> elms = new LinkedList<Element>();
//	
//	@SuppressWarnings({ "unchecked", "unused" })
//	@Override
//	public CompileResult compile(ConfigInfo info, String text) throws CompileException {
//	    try {
//	    	elms.clear();
//	    	ScriptEngineManager m = new ScriptEngineManager();
//	    	NashornScriptEngine engine = (NashornScriptEngine) m.getEngineByName("nashorn");
//	    	new NashornScriptEngineFactory().getScriptEngine(className -> false);
//	        ScriptContext context = engine.getContext();
//	    	
//	        if (engine==null) throw new CompileException("Unable to load JavaScriptEngine!");
//	        
//	        engine.put("getVal", (Function<String, Object>)t -> {
//				try {return getVariable(t);} catch (CompileException e) {e.printStackTrace();}
//				return null;
//			});
//	        engine.put("setVal", (BiConsumer<String,Object>)this::put);
//	        engine.put("slotElement", ((Consumer4<Integer,Integer,Integer,Integer>)this::slotelement));
//	        Consumer4<Integer,Integer,Integer,Integer> slot3 = (s,x,y,scale) -> {
//	        	Hudder.LOGGER.info("r");
//				elms.add(new ItemElement(x,y,player.getInventory().getStack(s),scale, true));
//				return 0;
//	        };
//	        engine.put("slotElement3", slot3);
//	        context.setAttribute("slotElement3", slot3, ScriptContext.ENGINE_SCOPE);
//	    	// read script file
//	    	engine.eval(PRESCRIPT);
//	    	engine.eval(text);
//	    	Hudder.LOGGER.info(engine.get("slotElement3").toString());
//	    	Hudder.LOGGER.info(((Consumer4<Integer,Integer,Integer,Integer>)engine.get("slotElement3")).toString());
//	    	((Consumer4<Integer,Integer,Integer,Integer>)engine.get("slotElement3")).accept(1, 1, 1, 1);
//    	    
//	    	//engine.invokeFunction("slotElement3", 1,1,1,1);
//
//	    	String TL = String.valueOf(engine.invokeFunction("topleft", info));
//	    	String BL = String.valueOf(engine.invokeFunction("bottomleft", info));
//	    	String TR = String.valueOf(engine.invokeFunction("topright", info));
//	    	String BR = String.valueOf(engine.invokeFunction("bottomright", info));
//	    	
//		    return new CompileResult(TL, 1, BL, 1, TR, 1, BR, 1, new Element[0]);  //This one works.   
//		} catch (ScriptException | NoSuchMethodException e) {
//			e.printStackTrace();
//			throw new CompileException(e.getLocalizedMessage());
//		} catch (CompileException e) {
//			throw e; //Double it and give it to the next person
//		}
//	}
}