package dev.ngspace.hudder.hudderv3.asm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.Hudder;
import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableFunction;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.Binder;
import dev.ngspace.hudder.compilers.HudderV3Compiler;
import dev.ngspace.hudder.hudderv3.GeneratedCompiler;
import dev.ngspace.hudder.hudderv3.HudderV3Helper;
import dev.ngspace.hudder.utils.ObjectWrapper;

public class V3ClassWriter implements Binder {
	
	public ClassWriter classWriter;
	public String classname;
	public V3MethodWriter init;
	private final Set<String> generatedApiFunctions = new HashSet<>();
	
	public V3ClassWriter(String classname) {
		this.classname = classname;
		classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		classWriter.visit(Opcodes.V25, Opcodes.ACC_PUBLIC, classname, null,
				"dev/ngspace/hudder/compilers/abstractions/AVarTextCompiler", new String[] {
						Type.getInternalName(GeneratedCompiler.class)
				});
	    
	    initPublicField("uimanager", ArrayElementManager.class);
	}
	
	private void initPublicField(String name, Class<?> type) {
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
		
		classWriter.newField(classname, "v3compiler", Type.getDescriptor(HudderV3Compiler.class));
		
	    classWriter.visitField(
	            Opcodes.ACC_PUBLIC,
	            "v3compiler",
	            Type.getDescriptor(HudderV3Compiler.class),
	            null,
	            null
	    ).visitEnd();

		
//		init = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>",
//				"(Ldev/ngspace/hudder/compilers/HudderV3Compiler;)V", null, null);
		init = createMethod("<init>", new Class<?>[] {HudderV3Compiler.class}, null, null, null);
//		init.visitCode();

		init.aload(0);
		init.methodVisitor.visitMethodInsn(
		        Opcodes.INVOKESPECIAL,
		        "dev/ngspace/hudder/compilers/abstractions/AVarTextCompiler",
		        "<init>",
		        "()V",
		        false
		);
		
		// Init v3compiler field
		init.aload(0);
		init.aload(1);
		init.methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, classname, "v3compiler",
				Type.getDescriptor(HudderV3Compiler.class));
	}
	
	public V3ExecuteMethodWriter createExecuteMethod(String name, Class<?>[] classes) {
		return new V3ExecuteMethodWriter(this, name, classes);
	}
	
	public V3MethodWriter createMethod(String name, Class<?>[] parameters, Class<?> returntype, String signature,
			String[] exceptions) {
		return new V3MethodWriter(this, name, parameters, returntype, signature, exceptions);
	}
	
	public Class<?> toClass() {
		
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
		ByteArrayClassLoader classLoader = new ByteArrayClassLoader(getClass().getClassLoader());
		
		return classLoader.define(classname.replace('/', '.'), bytecode);
	}
	@Override
	public void bindConsumer(BindableConsumer cons, String... names) {
		for (String name : names) {
			HudderV3Helper.api_consumers.put(name.toLowerCase(), cons);
		}
	}
	@Override
	public void bindFunction(BindableFunction cons, String... names) {
		for (String name : names) {
			String func = "api_function_" + name.toLowerCase();

			// The registry is global, but generated methods belong to this class writer.
			// Keep first-registration-wins semantics without suppressing wrappers in
			// every GeneratedClass created after the first one.
			HudderV3Helper.api_functions.putIfAbsent(func, cons);
			if (!generatedApiFunctions.add(func)) {
				continue;
			}
			initPublicField(func, BindableFunction.class);
			init.aload(0);
			init.loadConstant(func);
			init.callStatic(HudderV3Helper.class, "getApiFunction", "(Ljava/lang/String;)"
					+ "Ldev/ngspace/hudder/api/functionsandconsumers/FunctionAndConsumerAPI$BindableFunction;",
					false);
			init.putField(func, BindableFunction.class);
			
			V3MethodWriter apiwrapper = createMethod(func,
					new Class<?>[] {ObjectWrapper[].class},
					Object.class, null, null);
			apiwrapper.aload(0);
			apiwrapper.getField(func, BindableFunction.class);
			apiwrapper.aload(0);
			apiwrapper.getField("uimanager", ArrayElementManager.class);
			apiwrapper.aload(0);
			apiwrapper.getField("v3compiler", HudderV3Compiler.class);
			apiwrapper.aload(1);
			apiwrapper.callInterface(BindableFunction.class, "invoke",
					"(Ldev/ngspace/hudder/api/functionsandconsumers/IUIElementManager;Ldev/ngspace/hudder/compilers/abstractions/AHudCompiler;[Ldev/ngspace/hudder/utils/ObjectWrapper;)Ljava/lang/Object;");
			apiwrapper.end(Opcodes.ARETURN);
		}
	}
}