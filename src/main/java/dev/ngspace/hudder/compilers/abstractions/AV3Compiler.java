package dev.ngspace.hudder.compilers.abstractions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI;
import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.V3ClassWriter;
import dev.ngspace.hudder.hudderv3.V3ExecuteMethodWriter;
import dev.ngspace.hudder.hudderv3.V3HudInformation;
import dev.ngspace.hudder.hudderv3.V3VariableProcessor;
import dev.ngspace.hudder.hudderv3.instructions.variables.VariableVisitor;
import dev.ngspace.hudder.v2runtime.functions.HudderFunctions;

public abstract class AV3Compiler extends AVarTextCompiler {
	public Map<String, String> user_methods = new HashMap<String, String>();
	public Map<String, String> user_functions = new HashMap<String, String>();
	int user_defines_count = 0;

	
	public V3VariableProcessor variableProcessor = new V3VariableProcessor();

	@Override
	public HudInformation execute(HudderConfig info, String processedfile, String filename) throws ExecutionException {
		
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
		
		compile(executeMethod, classWriter, info, processedfile, filename, end);
		
		executeMethod.putLabel(end);
		executeMethod.end();
		
		Class<?> dynamicClass = classWriter.toClass();
		
		try {
			Object instance = dynamicClass.getDeclaredConstructor(getClass()).newInstance(this);
			Method method = dynamicClass.getMethod("execute", HudderConfig.class, String.class, String.class);


			return ((V3HudInformation) method.invoke(instance, info, processedfile, filename)).hudInformation;
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException re)
				throw re;
			e.printStackTrace();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		
		return HudInformation.of("failed");
	}
	
	public abstract boolean compile(V3ExecuteMethodWriter executeMethod, V3ClassWriter classWriter, HudderConfig info,
			String text, String filename, Label end) throws ExecutionException;
	

	public void defineFunctionOrMethod(V3ClassWriter writer, String commands, String[] args, HudderConfig info,
			String name, String filename)
			throws ExecutionException {
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

	public VariableVisitor parseVariable(String string) throws ExecutionException {
		return variableProcessor.parseVariable(string, this);
	}
}
