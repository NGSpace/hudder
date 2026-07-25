package dev.ngspace.hudder.hudderv3;

import org.objectweb.asm.Opcodes;

public class V3ExecuteMethodWriter extends V3MethodWriter {

	public V3ExecuteMethodWriter(V3ClassWriter classWriter) {
		super(classWriter, "execute",
				"("
				+ "Ldev/ngspace/hudder/config/HudderConfig;"
				+ "Ljava/lang/String;"
				+ "Ljava/lang/String;"
				+ ")Ldev/ngspace/hudder/compilers/utils/HudInformation;", null, new String[] {
						"dev/ngspace/hudder/exceptions/ExecutionException"
				});
		
		
		// Create StringBuilder
		methodVisitor.visitTypeInsn(Opcodes.NEW, STRING_BUILDER);
		methodVisitor.visitInsn(Opcodes.DUP);
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, STRING_BUILDER, "<init>", "()V", false);
		methodVisitor.visitVarInsn(Opcodes.ASTORE, 4);
	}
	


	public void appendDataVariableRegistryCall(String variable) {
		loadBuilder();
		callDataVariableRegistry(variable);
		appendToBuilderAndPop();
	}



	public void appendStringConstant(String string) {
		loadBuilder();
		loadConstant(string);
		appendToBuilderAndPop();
	}
	
	public void loadBuilder() {
		aload(4);
	}

	public void appendToBuilderAndPop() {
		appendToBuilder();
		pop();
	}



	public void end() {
		// Return HudInformation.of(StringBuilder)
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 4);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "toString", "()Ljava/lang/String;", false);
		methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "dev/ngspace/hudder/compilers/utils/HudInformation", "of",
				"(Ljava/lang/String;)Ldev/ngspace/hudder/compilers/utils/HudInformation;", false);
		methodVisitor.visitInsn(Opcodes.ARETURN);
		
		methodVisitor.visitMaxs(0, 0);
		methodVisitor.visitEnd();
	}
	
}
