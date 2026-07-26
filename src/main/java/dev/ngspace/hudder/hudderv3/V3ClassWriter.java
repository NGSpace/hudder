package dev.ngspace.hudder.hudderv3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.api.functionsandconsumers.ArrayElementManager;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableConsumer;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.BindableFunction;
import dev.ngspace.hudder.api.functionsandconsumers.FunctionAndConsumerAPI.Binder;
import dev.ngspace.hudder.compilers.HudderV3Compiler;

public class V3ClassWriter implements Binder {
	public ClassWriter classWriter;
	public String classname;
	public V3ClassWriter(String classname) {
		this.classname = classname;
		classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		classWriter.visit(Opcodes.V25, Opcodes.ACC_PUBLIC, classname, null,
				"dev/ngspace/hudder/compilers/abstractions/AVarTextCompiler", null);
		
		classWriter.newField(classname, "uimanager", Type.getDescriptor(ArrayElementManager.class));
		
	    classWriter.visitField(
	            Opcodes.ACC_PUBLIC,
	            "uimanager",
	            Type.getDescriptor(ArrayElementManager.class),
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

		
		MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>",
				"(Ldev/ngspace/hudder/compilers/HudderV3Compiler;)V", null, null);
		methodVisitor.visitCode();

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitMethodInsn(
		        Opcodes.INVOKESPECIAL,
		        "dev/ngspace/hudder/compilers/abstractions/AVarTextCompiler",
		        "<init>",
		        "()V",
		        false
		);
		
		// Init v3compiler field
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
		methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, classname, "v3compiler",
				Type.getDescriptor(HudderV3Compiler.class));
		
		// Init UIElements field
	    
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitTypeInsn(Opcodes.NEW, Type.getInternalName(ArrayElementManager.class));
		methodVisitor.visitInsn(Opcodes.DUP);
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(ArrayElementManager.class),
				"<init>", "()V", false);
		methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, classname, "uimanager",
				Type.getDescriptor(ArrayElementManager.class));
		
		methodVisitor.visitInsn(Opcodes.RETURN);
		methodVisitor.visitMaxs(0, 0);
		methodVisitor.visitEnd();
	}
	
	public V3ExecuteMethodWriter createExecuteMethod() {
		return new V3ExecuteMethodWriter(this);
	}
	
	public V3MethodWriter createMethod(String name, Class<?>[] parameters, Class<?> returntype, String signature,
			String[] exceptions) {
		return new V3MethodWriter(this, name, parameters, returntype, signature, exceptions);
	}
	
	public Class<?> toClass() {
		classWriter.visitEnd();
		byte[] bytecode = classWriter.toByteArray();
		if (!new File("hudderv3output.class").exists()) {
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
		
	}
	@Override
	public void bindFunction(BindableFunction cons, String... names) {
		for (String name : names) {
			HudderV3Helper.api_functions.put(name, cons);
			V3MethodWriter writer = createMethod("api_function_" + name,
					new Class<?>[] {String.class, Object[].class},
					Object.class,
					null, new String[] {
						"dev/ngspace/hudder/exceptions/ExecutionException"
					});
			writer.loadConstant(name);
			writer.aload(0);
			writer.getField("uimanager", ArrayElementManager.class);
			writer.aload(0);
			writer.getField("v3compiler", HudderV3Compiler.class);
			writer.aload(1);
			writer.aload(2);
			writer.callStatic(HudderV3Helper.class, "callApiFunction",
					"(Ljava/lang/String;Ldev/ngspace/hudder/api/functionsandconsumers/ArrayElementManager;Ldev/ngspace/hudder/compilers/abstractions/AVarTextCompiler;[Ljava/lang/Object;)Ljava/lang/Object;", false);
		}
	}
}
