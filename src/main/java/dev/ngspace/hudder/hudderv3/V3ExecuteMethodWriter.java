package dev.ngspace.hudder.hudderv3;

import org.objectweb.asm.Opcodes;

import dev.ngspace.hudder.compilers.utils.HudInformation;
import dev.ngspace.hudder.config.HudderConfig;

public class V3ExecuteMethodWriter extends V3MethodWriter {

	public V3ExecuteMethodWriter(V3ClassWriter classWriter) {
		super(classWriter, "execute",
				new Class<?>[] {
					HudderConfig.class,
					String.class,
					String.class
				}, HudInformation.class, null, new String[] {
					"dev/ngspace/hudder/exceptions/ExecutionException"
				});
		
		variableindex+=3; // the parameters
		
		
		// Create StringBuilder
		methodVisitor.visitTypeInsn(Opcodes.NEW, STRING_BUILDER);
		methodVisitor.visitInsn(Opcodes.DUP);
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, STRING_BUILDER, "<init>", "()V", false);
		methodVisitor.visitVarInsn(Opcodes.ASTORE, ++variableindex);
	}



	public void appendStringConstant(String string) {
		loadBuilder();
		loadConstant(string);
		appendToBuilder();
	}
	
	public void loadBuilder() {
		aload(4);
	}

	public void appendToBuilderAndPop() {
		appendToBuilder();
		pop();
	}



	@Override
	public void end() {
		// Return HudInformation.of(StringBuilder)
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 4);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "toString", "()Ljava/lang/String;", false);
		methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "dev/ngspace/hudder/compilers/utils/HudInformation", "of",
				"(Ljava/lang/String;)Ldev/ngspace/hudder/compilers/utils/HudInformation;", false);
		
		
		super.end(Opcodes.ARETURN);
	}
	
}
