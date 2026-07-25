package dev.ngspace.hudder.hudderv3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class V3ClassWriter {
	public ClassWriter classWriter;
	public String classname;
	public V3ClassWriter(String classname) {
		this.classname = classname;
		classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		classWriter.visit(Opcodes.V25, Opcodes.ACC_PUBLIC, classname, null,
				"dev/ngspace/hudder/compilers/abstractions/AVarTextCompiler", null);
	}
	public void createDummyInit() {
		MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		methodVisitor.visitCode();

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitMethodInsn(
		        Opcodes.INVOKESPECIAL,
		        "dev/ngspace/hudder/compilers/abstractions/AVarTextCompiler",
		        "<init>",
		        "()V",
		        false
		);
		
		methodVisitor.visitInsn(Opcodes.RETURN);
		methodVisitor.visitMaxs(0, 0);
		methodVisitor.visitEnd();
	}
	
	public V3ExecuteMethodWriter createExecuteMethod() {
		return new V3ExecuteMethodWriter(this);
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
		
		return classLoader.define(classname, bytecode);
	}
}
