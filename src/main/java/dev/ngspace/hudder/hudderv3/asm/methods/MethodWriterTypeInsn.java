package dev.ngspace.hudder.hudderv3.asm.methods;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public interface MethodWriterTypeInsn extends MethodWriterBase {

	public default void checkcast(Class<?> type) {
		visitor().visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(type));
	}

	public default void newArray(Class<?> type) {
		visitor().visitTypeInsn(Opcodes.ANEWARRAY,
				Type.getInternalName(type));
	}

	public default void newInsn(Class<?> type) {
		visitor().visitTypeInsn(Opcodes.NEW, Type.getInternalName(type));
	}

	public default void newAndDup(Class<?> type) {
		newInsn(type);
		dup();
	}
	
	public default void instanceOf(Class<?> type) {
		visitor().visitTypeInsn(Opcodes.INSTANCEOF, Type.getInternalName(type));
	}
}
