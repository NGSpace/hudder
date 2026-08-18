package dev.ngspace.hudder.hudderv3.asm.methods;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public interface MethodWriterBase {
	
	public MethodVisitor visitor();
	
	public default void pop() {
		visitor().visitInsn(Opcodes.POP);
	}
	
	public default void dup() {
		visitor().visitInsn(Opcodes.DUP);
	}
	
	public default void dupX1() {
		visitor().visitInsn(Opcodes.DUP_X1);
	}
	
	public default void dupX2() {
		visitor().visitInsn(Opcodes.DUP_X2);
	}
	
	public default void dup2() {
		visitor().visitInsn(Opcodes.DUP2);
	}
	
	public default void dup2X1() {
		visitor().visitInsn(Opcodes.DUP2_X1);
	}
	
	public default void dup2X2() {
		visitor().visitInsn(Opcodes.DUP2_X2);
	}
	
	public default void swap() {
		visitor().visitInsn(Opcodes.SWAP);
	}
	
	public default void athrow() {
		visitor().visitInsn(Opcodes.ATHROW);
	}
	
	public default void tryCatch(Label try_start, Label try_end, Label handler_start, Class<?> exception) {
		visitor().visitTryCatchBlock(try_start, try_end, handler_start, Type.getInternalName(exception));
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
