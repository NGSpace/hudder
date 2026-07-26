package dev.ngspace.hudder.hudderv3;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

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

	public void appendToBuilder() {
		Label end = new Label();
		Label append = new Label();
		Label append_double = new Label();
		
		int value_index = astore();
		aload(value_index);
		methodVisitor.visitTypeInsn(Opcodes.INSTANCEOF, Type.getInternalName(Number.class));
		methodVisitor.visitJumpInsn(Opcodes.IFEQ, append);

		aloadDouble(value_index);
		
		methodVisitor.visitLdcInsn(1d);
		methodVisitor.visitInsn(Opcodes.DREM);
		methodVisitor.visitLdcInsn(0d);
		methodVisitor.visitInsn(Opcodes.DCMPG);
		methodVisitor.visitJumpInsn(Opcodes.IFNE, append_double);

		aload(value_index);
	    methodVisitor.visitTypeInsn(
	            Opcodes.CHECKCAST,
	            Type.getInternalName(Number.class)
	    );
		methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/Number",
				"longValue",
				"()J",
				false
		);
		int long_index = lstore();
		loadBuilder();
		lload(long_index);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "append",
				"(J)L"+STRING_BUILDER+";", false);
		methodVisitor.visitJumpInsn(Opcodes.GOTO, end);
		
		methodVisitor.visitLabel(append_double);
		loadBuilder();
		aload(value_index);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "append",
				"(Ljava/lang/Object;)L"+STRING_BUILDER+";", false);
		methodVisitor.visitJumpInsn(Opcodes.GOTO, end);

		methodVisitor.visitLabel(append);
		loadBuilder();
		aload(value_index);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "append",
				"(Ljava/lang/Object;)L"+STRING_BUILDER+";", false);
		
		methodVisitor.visitLabel(end);
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
