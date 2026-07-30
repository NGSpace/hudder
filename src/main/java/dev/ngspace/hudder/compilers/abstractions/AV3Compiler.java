package dev.ngspace.hudder.compilers.abstractions;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.CompileException;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.GeneratedCompiler;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.V3VariableProcessor;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;
import dev.ngspace.hudder.utils.HudFileUtils;
import dev.ngspace.hudder.v2runtime.functions.HudderFunctions;

public abstract class AV3Compiler extends AVarTextCompiler {
	public Map<String, String> user_methods = new HashMap<String, String>();
	public Map<String, String> user_functions = new HashMap<String, String>();
	int user_defines_count = 0;

	
	public Map<String, CachedCompiler> cache = new HashMap<String, CachedCompiler>();
	
	protected AV3Compiler() {
		HudFileUtils.addReloadResourcesListener(()->{
			for(CachedCompiler c:cache.values()) c.close();
			cache.clear();
		});
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
		
		compile(executeMethod, classWriter, Hudder.config, text, filepath, end);
		
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
	
	public abstract boolean compile(V3ExecuteMethodWriter executeMethod, V3ClassWriter classWriter, HudderConfig info,
			String text, String filename, Label end) throws CompileException;
	

	public void defineFunctionOrMethod(V3ClassWriter writer, String commands, String[] args, HudderConfig info,
			String name, String filename)
			throws CompileException {
		user_defines_count++;
		String finalname = "user_" + name + "_" + user_defines_count;
		var method = writer.createExecuteMethod(finalname, new Class<?>[] {
			HudderConfig.class,
			String.class,
			String.class,
			Object[].class
		});
		
		for (int i = 0;i<args.length;i++) {
			method.defineVariable(args[i].toLowerCase().trim());
			method.defineVariable("arg" + (i+1));
			method.aload(4);
			method.loadConstantUnsafe(i);
			method.methodVisitor.visitInsn(Opcodes.AALOAD);
			method.dup();
			method.storeVariable(args[i].toLowerCase().trim());
			method.storeVariable("arg" + (i+1));
		}

		Label end = new Label();
		boolean hasReturn = compile(method, writer, info, commands, filename, end);
		method.putLabel(end);
		method.end();
		
		if (!hasReturn)
			user_methods.put(name, finalname);
		else
			user_functions.put(name, finalname);
		
	}

	public VariableVisitor parseVariable(String string) throws CompileException {
		return variableProcessor.parseVariable(string, this);
	}
	
	

	public static record CachedCompiler(Object compiledhud, GeneratedCompiler generatedCompiler, CompileException exception) implements Closeable{
		@Override
		public void close() throws IOException {
			/* Right now the v3 can cause memory leaks because it loads classes but never unloads and deletes them */
		}
	}
}
