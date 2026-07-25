package dev.ngspace.hudder.hudderv3;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.api.variableregistry.DataVariableRegistry;
import dev.ngspace.hudder.compilers.abstractions.AVarTextCompiler;

public class V3MethodWriter {
	
	public static final String STRING_BUILDER = Type.getInternalName(StringBuilder.class);
	public static final String VAR_REGISTRY = Type.getInternalName(DataVariableRegistry.class);
	
	public V3ClassWriter classWriter;
	public MethodVisitor methodVisitor;
	public int variableindex;

	public V3MethodWriter(V3ClassWriter classWriter, String name, String descriptior, String signature,
			String[] exceptions) {
		this.classWriter = classWriter;
		
		this.methodVisitor = classWriter.classWriter.visitMethod(Opcodes.ACC_PUBLIC, name, descriptior,
				signature, exceptions);
		methodVisitor.visitCode();
	}
	
	public void aload(int index) {
		methodVisitor.visitVarInsn(Opcodes.ALOAD, index);
	}
	
	public void loadConstant(Object constant) {
		methodVisitor.visitLdcInsn(constant);
	}
	public void loadConstant(double constant) {
		methodVisitor.visitLdcInsn(constant);

		methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"java/lang/Double",
				"valueOf",
				"(D)Ljava/lang/Double;",
				false
		);
	}
	
	public int store() {
		methodVisitor.visitVarInsn(Opcodes.ASTORE, ++variableindex);
		return variableindex;
	}
	
	
	public void pop() {
		methodVisitor.visitInsn(Opcodes.POP);
	}

	public void callDataVariableRegistry(String variable) {
		loadConstant(variable.toLowerCase());
		callDataVariableRegistry();
	}

	public void callDataVariableRegistry() {
		methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, VAR_REGISTRY, "getAny",
				"(Ljava/lang/String;)Ljava/lang/Object;", false);
	}

	public void appendToBuilder() {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "append",
				"(Ljava/lang/Object;)L"+STRING_BUILDER+";", false);
	}

	public void callStatic(Class<?> clazz, String name, String descriptor, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(clazz), name, descriptor,
				isInterface);
	}

	public void call(Class<AVarTextCompiler> clazz, String name, String descriptor, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(clazz), name, descriptor,
				isInterface);
	}

	public void getStatic(Class<?> clazz, String name, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(clazz), name,
				Type.getInternalName(type));
	}
}
