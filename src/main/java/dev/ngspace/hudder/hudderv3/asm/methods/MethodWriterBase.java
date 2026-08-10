package dev.ngspace.hudder.hudderv3.asm.methods;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public interface MethodWriterBase {
	
	public MethodVisitor visitor();
	
	public default void pop() {
		visitor().visitInsn(Opcodes.POP);
	}
	
	public default void dup() {
		visitor().visitInsn(Opcodes.DUP);
	}
	
	public default void dup2() {
		visitor().visitInsn(Opcodes.DUP2);
	}
	
	// Comparison instructions
	
	public default void lcmp() {
		visitor().visitInsn(Opcodes.LCMP);
	}
	
	public default void fcmpl() {
		visitor().visitInsn(Opcodes.FCMPL);
	}
	
	public default void fcmpg() {
		visitor().visitInsn(Opcodes.FCMPG);
	}
	
	public default void dcmpl() {
		visitor().visitInsn(Opcodes.DCMPL);
	}
	
	public default void dcmpg() {
		visitor().visitInsn(Opcodes.DCMPG);
	}
	
	// Value instructions
	
	public default void booleanValue() {
		visitor().visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
	}
	
	public default void intValue() {
		visitor().visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
	}
	
	public default void doubleValue() {
		visitor().visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
	}
	
	public default void floatValue() {
		visitor().visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", false);
	}
	
	public default void longValue() {
		visitor().visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J", false);
	}
}
