package dev.ngspace.hudder.compilers.abstractions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.PositionedBinder;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.GeneratedCompiler;
import dev.ngspace.hudder.hudderv3.HudderAPIFunctions;
import dev.ngspace.hudder.hudderv3.HudderAPIMethods;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.hudderv3.TokenizedCodeBlock;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.WalkingV3ExpressionParser;
import dev.ngspace.hudder.hudderv3.instructions.V3ExpressionParser;
import dev.ngspace.hudder.hudderv3.instructions.variables.ExpressionVisitor;

public abstract class AV3Compiler extends AVarTextCompiler implements PositionedBinder {
	
	public static final String VERIFIER_ERROR_NOTE = """
			
			# This is a bytecode-gen error!
			# Please report this to the developer of Hudder!""";
	
	public Map<String, CachedCompiler> cache = new HashMap<String, CachedCompiler>();
	public V3ExpressionParser expressionParser = new WalkingV3ExpressionParser();
	public boolean system_variables = true;
	
	protected AV3Compiler() {
		FunctionAndConsumerAPI.getInstance().applyFunctionsAndConsumers(this);
		HudderAPIFunctions.bindAllAPIFunctions(this);
		HudderAPIMethods.bindAllAPIMethods(this);
	}
	
	@Override
	public void compileFile(String text, String filepath) throws CompileException {
		if (cache.containsKey(text)) {
			var cachehit = cache.get(text);
			if (cachehit.exception!=null) throw cachehit.exception;
			return;
		}
		try {
		
			V3ClassWriter classWriter = new V3ClassWriter("dev/ngspace/hudder/hudderv3/GeneratedClass",
					filepath);
			classWriter.createInit();
			
			V3ExecuteMethodWriter executeMethod = classWriter.createExecuteMethod("execute", new Class<?>[] {
					HudderConfig.class,
					String.class,
					String.class
				});
			
			Label end = new Label();
			
			executeMethod.aload(0);
			executeMethod.getField("uimanager", ArrayElementManager.class);
			executeMethod.callInterface(List.class, "clear", "()V");
			
			compile(Hudder.config, text, filepath, new TextPos(0, 0)).writeInstructions(executeMethod, classWriter, end);
			
			executeMethod.putLabel(end);
			executeMethod.end();
		
			Class<?> dynamicClass = classWriter.toClass();
			
			Object instance = dynamicClass.getDeclaredConstructor(AV3Compiler.class).newInstance(this);
			cache.put(text, new CachedCompiler(instance, (GeneratedCompiler) instance, null));
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException re)
				throw re;
			e.printStackTrace();
			var ne = new CompileException(e.getTargetException().toString(),-1, -1, e.getTargetException());
			cache.put(text, new CachedCompiler(null,null,ne));
			throw ne;
		} catch (ClassFormatError e) {
			// The compilation manager does not handle JVM errors and will crash the game which is bad.
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (VerifyError e) {
			// The compilation manager does not handle Verifier errors and will crash the game which is bad.
			e.printStackTrace();
			String msg = VERIFIER_ERROR_NOTE + e.getMessage();
			int frame = msg.indexOf("Current Frame");
			int at = msg.indexOf("@");
			throw new RuntimeException(msg.substring(0, msg.indexOf('\n')+1) +
					'\n' +
					msg.substring(at==-1?0:at, frame==-1?msg.length():frame));
		} catch (CompileException e) {
			cache.put(text, new CachedCompiler(null,null,e));
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			var ne = new CompileException(e.toString(),-1, -1, e);
			cache.put(text, new CachedCompiler(null,null,ne));
			throw ne;
		} 
	}

	@Override
	public HudInformation execute(HudderConfig info, String processedfile, String filename) throws ExecutionException {
		try {
			return cache.get(processedfile).generatedCompiler().execute(info, processedfile, filename).hudInformation;
		} catch (VerifyError e) {
			// The compilation manager does not handle Verifier errors and will crash the game which is bad.
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public abstract TokenizedCodeBlock compile(HudderConfig info, String text, String filename, TextPos offset)
			throws CompileException;
	
	
	
	public ExpressionVisitor parseVariable(String string, TextPos pos) throws CompileException {
		return expressionParser.parseExpression(string, this, pos);
	}
	
	
	
	public V3ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	public void setExpressionParser(V3ExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
	}



	public static record CachedCompiler(Object compiledhud, GeneratedCompiler generatedCompiler, CompileException exception) {}
	
	@Override
	public void resetState() throws IOException {
		system_variables = true;
		for (var instance : cache.values())
			if (instance.generatedCompiler instanceof AVarTextCompiler comp)
				comp.shutdown();
		cache.clear();
		super.resetState();
	}

	@Override
	public void bindConsumer(BindablePositionedConsumer cons, String... names) {
		for (String name : names) {
			HudderV3Helper.api_consumers.put("api_consumer_" + name, cons);
		}
	}
	
	@Override
	public void bindFunction(BindablePositionedFunction cons, String... names) {
		for (String name : names) {
			HudderV3Helper.api_functions.put("api_function_" + name, cons);
		}
	}
}
