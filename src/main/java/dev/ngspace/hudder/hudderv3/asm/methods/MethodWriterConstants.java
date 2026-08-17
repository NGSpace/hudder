package dev.ngspace.hudder.hudderv3.asm.methods;

import org.objectweb.asm.Opcodes;

public interface MethodWriterConstants extends MethodWriterBase {

	public default void nullConstant() {
		visitor().visitInsn(Opcodes.ACONST_NULL);
	}
	
	public default void loadConstant(Object constant) {
		visitor().visitLdcInsn(constant);
	}
	public default void loadConstant(double constant) {
		visitor().visitLdcInsn(constant);
		// Preserve Double values even when they have no fractional part. V2 represents
		// every numeric literal as a Double, and functions such as str() expose that
		// distinction through Double.toString() (for example, str(10) -> "10.0").
		visitor().visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"java/lang/Double",
				"valueOf",
				"(D)Ljava/lang/Double;",
				false
		);
	}
	public default void loadConstant(float constant) {
		if (constant%1==0) {
			loadConstant((long)constant);
		} else {
			visitor().visitLdcInsn(constant);
			//Convert to Object
			visitor().visitMethodInsn(
					Opcodes.INVOKESTATIC,
					"java/lang/Float",
					"valueOf",
					"(F)Ljava/lang/Float;",
					false
			);
		}
	}
	public default void loadConstant(long constant) {
		visitor().visitLdcInsn(constant);
		//Convert to Object
		visitor().visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"java/lang/Long",
				"valueOf",
				"(J)Ljava/lang/Long;",
				false
		);
	}
	public default void loadConstantUnsafe(Object constant) {
		visitor().visitLdcInsn(constant);
	}
	public default void loadConstant(boolean constant) {
		visitor().visitLdcInsn(constant);
		//Convert to Object
		visitor().visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"java/lang/Boolean",
				"valueOf",
				"(Z)Ljava/lang/Boolean;",
				false
			);
	}
}
