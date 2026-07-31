package dev.ngspace.hudder.compilers.abstractions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Label;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.GeneratedCompiler;
import dev.ngspace.hudder.hudderv3.TokenizedCodeBlock;
import dev.ngspace.hudder.hudderv3.V3VariableProcessor;
import dev.ngspace.hudder.hudderv3.asm.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.asm.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.v2runtime.functions.HudderFunctions;

public abstract class AV3Compiler extends AVarTextCompiler {
	
	public Map<String, String> user_methods = new HashMap<String, String>();
	public Map<String, String> user_functions = new HashMap<String, String>();
	
	public Map<String, CachedCompiler> cache = new HashMap<String, CachedCompiler>();
	
	protected AV3Compiler() {
		HudFileUtils.addReloadResourcesListener(cache::clear);
	}
	
	public V3VariableProcessor variableProcessor = new V3VariableProcessor();
	
	@Override
	public void compileFile(String text, String filepath) throws CompileException {
		if (cache.containsKey(text)) {
			var cachehit = cache.get(text);
			if (cachehit.exception!=null) throw cachehit.exception;
			return;
		}
		
		V3ClassWriter classWriter = new V3ClassWriter("dev/ngspace/hudder/hudderv3/GeneratedClass");
		classWriter.createInit();
		
		FunctionAndConsumerAPI.getInstance().applyFunctionsAndConsumers(classWriter);
		HudderFunctions.bindAllAPIFunctions(classWriter);
		
		V3ExecuteMethodWriter executeMethod = classWriter.createExecuteMethod("execute", new Class<?>[] {
				HudderConfig.class,
				String.class,
				String.class
			});
		
		Label end = new Label();
		
		// Init UIElements field
	    
		executeMethod.aload(0);
		executeMethod.newAndDup(ArrayElementManager.class);
		executeMethod.callSpecial(ArrayElementManager.class, "<init>", "()V", false);
		executeMethod.putField("uimanager", ArrayElementManager.class);
		
		compile(Hudder.config, text, filepath).writeInstructions(executeMethod, classWriter, end);
		
		executeMethod.putLabel(end);
		executeMethod.end();
		
		Class<?> dynamicClass = classWriter.toClass();
		
		
		try {
			Object instance = dynamicClass.getDeclaredConstructor(getClass()).newInstance(this);
			cache.put(text, new CachedCompiler(instance, (GeneratedCompiler) instance, null));
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException re)
				throw re;
			e.printStackTrace();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public HudInformation execute(HudderConfig info, String processedfile, String filename) throws ExecutionException {
		return cache.get(processedfile).generatedCompiler().execute(info, processedfile, filename).hudInformation;
	}
	
	public abstract TokenizedCodeBlock compile(HudderConfig info, String text, String filename)
			throws CompileException;
	

	public void defineFunctionOrMethod(V3ClassWriter writer, String commands, String[] args, HudderConfig info,
			String name, String filename)
			throws CompileException {
	}

	public VariableVisitor parseVariable(String string) throws CompileException {
		return variableProcessor.parseVariable(string, this);
	}
	
	

	public static record CachedCompiler(Object compiledhud, GeneratedCompiler generatedCompiler, CompileException exception) {}
}
