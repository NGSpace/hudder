package dev.ngspace.hudder.hudderv3.asm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
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

public class V3ClassWriter implements Binder {
	
	public ClassWriter classWriter;
	public String classname;
	
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
		
		methodVisitor.visitInsn(Opcodes.RETURN);
		methodVisitor.visitMaxs(0, 0);
		methodVisitor.visitEnd();
	}
	
	public V3ExecuteMethodWriter createExecuteMethod(String name, Class<?>[] classes) {
		return new V3ExecuteMethodWriter(this, name, classes);
	}
	
	public V3MethodWriter createMethod(String name, Class<?>[] parameters, Class<?> returntype, String signature,
			String[] exceptions) {
		return new V3MethodWriter(this, name, parameters, returntype, signature, exceptions);
	}
	
	public Class<?> toClass() {
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
			HudderV3Helper.api_consumers.put(name, cons);
		}
	}
	@Override
	public void bindFunction(BindableFunction cons, String... names) {
		for (String name : names) {
			HudderV3Helper.api_functions.put(name, cons);
		}
	}
}