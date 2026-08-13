package dev.ngspace.hudder.hudderv3.asm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.interfaces.BindablePositionedFunction;
import dev.ngspace.hudder.compilers.abstractions.AV3Compiler;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;
import dev.ngspace.hudder.hudderv3.GeneratedCompiler;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;

public class V3ClassWriter {
	
	public ClassWriter classWriter;
	public String classname;
	public V3MethodWriter init;
	private final Set<String> generatedApiFunctions = new HashSet<>();
	private final Set<String> generatedApiConsumers = new HashSet<>();
	private final Set<String> calledApiConsumers = new HashSet<>();
	private final Set<String> calledApiFunctions = new HashSet<>();
	
	public Map<String, String> user_methods = new HashMap<String, String>();
	public Map<String, String> user_functions = new HashMap<String, String>();
	
	public V3ClassWriter(String classname, String debugfilename) {
		this.classname = classname;
		classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		classWriter.visit(Opcodes.V25, Opcodes.ACC_PUBLIC, classname, null,
				"dev/ngspace/hudder/compilers/abstractions/AVarTextCompiler", new String[] {
						Type.getInternalName(GeneratedCompiler.class)
				});
		classWriter.visitSource(debugfilename, null);
		
	    initPublicField("uimanager", ArrayElementManager.class);
	}
	
	public void initPublicField(String name, Class<?> type) {
		classWriter.newField(classname, name, Type.getDescriptor(type));
		
	    classWriter.visitField(
	            Opcodes.ACC_PUBLIC,
	            name,
	            Type.getDescriptor(type),
	            null,
	            null
	    ).visitEnd();
	}
	public void createInit() {
		
		classWriter.newField(classname, "v3compiler", Type.getDescriptor(AV3Compiler.class));
		
	    classWriter.visitField(
	            Opcodes.ACC_PUBLIC,
	            "v3compiler",
	            Type.getDescriptor(AV3Compiler.class),
	            null,
	            null
	    ).visitEnd();

		
		init = createMethod("<init>", new Class<?>[] {AV3Compiler.class}, null, null, null);

		init.aload(0);
		init.callInit(AVarTextCompiler.class, "()V");
		
		// Init UIElements field
	    
		init.aload(0);
		init.newAndDup(ArrayElementManager.class);
		init.callSpecial(ArrayElementManager.class, "<init>", "()V", false);
		init.putField("uimanager", ArrayElementManager.class);
		
		// Init v3compiler field
		init.aload(0);
		init.aload(1);
		init.putField("v3compiler", AV3Compiler.class);
	}
	
	public V3ExecuteMethodWriter createExecuteMethod(String name, Class<?>[] classes) {
		return new V3ExecuteMethodWriter(this, name, classes);
	}
	
	public V3MethodWriter createMethod(String name, Class<?>[] parameters, Class<?> returntype, String signature,
			String[] exceptions) {
		return new V3MethodWriter(this, name, parameters, returntype, signature, exceptions);
	}
	
	public Class<?> toClass() {
		
		loadFunctions();
		loadConsumers();
		
		init.end(Opcodes.RETURN);
		
		classWriter.visitEnd();
		byte[] bytecode = classWriter.toByteArray();
		if ((!new File("hudderv3output.class").exists())&&Hudder.IS_DEBUG) {
			try (FileOutputStream writer = new FileOutputStream(new File("hudderv3output.class"))) {
				writer.write(bytecode);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		return new ByteArrayClassLoader(getClass().getClassLoader()).define(classname.replace('/', '.'), bytecode);
	}
	private void loadFunctions() {
		for (String name : calledApiFunctions) {
			String func = "api_function_" + name;

			if (!generatedApiFunctions.add(func)) {
				continue;
			}
			initPublicField(func, BindablePositionedFunction.class);
			init.aload(0);
			init.loadConstant(func);
			init.callStatic(HudderV3Helper.class, "getApiFunction", "(Ljava/lang/String;)"
					+ "Ldev/ngspace/hudder/api/functionsandconsumers/interfaces/BindablePositionedFunction;",
					false);
			init.putField(func, BindablePositionedFunction.class);
		}
	}
	
	public void loadConsumers() {
		for (String name : calledApiConsumers) {
			String func = "api_consumer_" + name;

			if (!generatedApiConsumers.add(func)) {
				continue;
			}
			initPublicField(func, BindablePositionedConsumer.class);
			init.aload(0);
			init.loadConstant(func);
			init.callStatic(HudderV3Helper.class, "getApiConsumer", "(Ljava/lang/String;)"
					+ "Ldev/ngspace/hudder/api/functionsandconsumers/interfaces/BindablePositionedConsumer;",
					false);
			init.putField(func, BindablePositionedConsumer.class);
		}
	}
	
	public void loadApiFunction(String name) {
		calledApiFunctions.add(name);
	}
	
	public void loadApiConsumer(String name) {
		calledApiConsumers.add(name);
	}
}