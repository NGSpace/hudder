package dev.ngspace.hudder.hudderv3.asm.methods;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public interface MethodWriterJumpInsn extends MethodWriterBase {

	public default void addAReturn() {
		visitor().visitInsn(Opcodes.ARETURN);
	}

	public default void putLabel(Label label) {
		visitor().visitLabel(label);
	}

	// Unconditional jumps

	public default void jumpto(Label jump) {
		visitor().visitJumpInsn(Opcodes.GOTO, jump);
	}

	public default void jsr(Label jump) {
		visitor().visitJumpInsn(Opcodes.JSR, jump);
	}

	// Single int / boolean comparisons against zero

	public default void ifeq(Label jump) {
		visitor().visitJumpInsn(Opcodes.IFEQ, jump);
	}

	public default void ifne(Label jump) {
		visitor().visitJumpInsn(Opcodes.IFNE, jump);
	}

	public default void iflt(Label jump) {
		visitor().visitJumpInsn(Opcodes.IFLT, jump);
	}

	public default void ifge(Label jump) {
		visitor().visitJumpInsn(Opcodes.IFGE, jump);
	}

	public default void ifgt(Label jump) {
		visitor().visitJumpInsn(Opcodes.IFGT, jump);
	}

	public default void ifle(Label jump) {
		visitor().visitJumpInsn(Opcodes.IFLE, jump);
	}

	// Integer comparisons

	public default void ifIcmpeq(Label jump) {
		visitor().visitJumpInsn(Opcodes.IF_ICMPEQ, jump);
	}

	public default void ifIcmpne(Label jump) {
		visitor().visitJumpInsn(Opcodes.IF_ICMPNE, jump);
	}

	public default void ifIcmplt(Label jump) {
		visitor().visitJumpInsn(Opcodes.IF_ICMPLT, jump);
	}

	public default void ifIcmpge(Label jump) {
		visitor().visitJumpInsn(Opcodes.IF_ICMPGE, jump);
	}

	public default void ifIcmpgt(Label jump) {
		visitor().visitJumpInsn(Opcodes.IF_ICMPGT, jump);
	}

	public default void ifIcmple(Label jump) {
		visitor().visitJumpInsn(Opcodes.IF_ICMPLE, jump);
	}

	// Reference comparisons

	public default void ifAcmpeq(Label jump) {
		visitor().visitJumpInsn(Opcodes.IF_ACMPEQ, jump);
	}

	public default void ifAcmpne(Label jump) {
		visitor().visitJumpInsn(Opcodes.IF_ACMPNE, jump);
	}

	// Null comparisons

	public default void ifnull(Label jump) {
		visitor().visitJumpInsn(Opcodes.IFNULL, jump);
	}

	public default void ifnonnull(Label jump) {
		visitor().visitJumpInsn(Opcodes.IFNONNULL, jump);
	}
}