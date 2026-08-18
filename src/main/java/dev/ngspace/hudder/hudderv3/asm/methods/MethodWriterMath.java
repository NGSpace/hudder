package dev.ngspace.hudder.hudderv3.asm.methods;

import org.objectweb.asm.Opcodes;

public interface MethodWriterMath extends MethodWriterBase {

	// Addition

	public default void iadd() {
		visitor().visitInsn(Opcodes.IADD);
	}

	public default void ladd() {
		visitor().visitInsn(Opcodes.LADD);
	}

	public default void fadd() {
		visitor().visitInsn(Opcodes.FADD);
	}

	public default void dadd() {
		visitor().visitInsn(Opcodes.DADD);
	}


	// Subtraction

	public default void isub() {
		visitor().visitInsn(Opcodes.ISUB);
	}

	public default void lsub() {
		visitor().visitInsn(Opcodes.LSUB);
	}

	public default void fsub() {
		visitor().visitInsn(Opcodes.FSUB);
	}

	public default void dsub() {
		visitor().visitInsn(Opcodes.DSUB);
	}


	// Multiplication

	public default void imul() {
		visitor().visitInsn(Opcodes.IMUL);
	}

	public default void lmul() {
		visitor().visitInsn(Opcodes.LMUL);
	}

	public default void fmul() {
		visitor().visitInsn(Opcodes.FMUL);
	}

	public default void dmul() {
		visitor().visitInsn(Opcodes.DMUL);
	}


	// Division

	public default void idiv() {
		visitor().visitInsn(Opcodes.IDIV);
	}

	public default void ldiv() {
		visitor().visitInsn(Opcodes.LDIV);
	}

	public default void fdiv() {
		visitor().visitInsn(Opcodes.FDIV);
	}

	public default void ddiv() {
		visitor().visitInsn(Opcodes.DDIV);
	}


	// Remainder

	public default void irem() {
		visitor().visitInsn(Opcodes.IREM);
	}

	public default void lrem() {
		visitor().visitInsn(Opcodes.LREM);
	}

	public default void frem() {
		visitor().visitInsn(Opcodes.FREM);
	}

	public default void drem() {
		visitor().visitInsn(Opcodes.DREM);
	}


	// Negation

	public default void ineg() {
		visitor().visitInsn(Opcodes.INEG);
	}

	public default void lneg() {
		visitor().visitInsn(Opcodes.LNEG);
	}

	public default void fneg() {
		visitor().visitInsn(Opcodes.FNEG);
	}

	public default void dneg() {
		visitor().visitInsn(Opcodes.DNEG);
	}


	// Left shift

	public default void ishl() {
		visitor().visitInsn(Opcodes.ISHL);
	}

	public default void lshl() {
		visitor().visitInsn(Opcodes.LSHL);
	}


	// Signed right shift

	public default void ishr() {
		visitor().visitInsn(Opcodes.ISHR);
	}

	public default void lshr() {
		visitor().visitInsn(Opcodes.LSHR);
	}


	// Unsigned right shift

	public default void iushr() {
		visitor().visitInsn(Opcodes.IUSHR);
	}

	public default void lushr() {
		visitor().visitInsn(Opcodes.LUSHR);
	}


	// Bitwise AND

	public default void iand() {
		visitor().visitInsn(Opcodes.IAND);
	}

	public default void land() {
		visitor().visitInsn(Opcodes.LAND);
	}


	// Bitwise OR

	public default void ior() {
		visitor().visitInsn(Opcodes.IOR);
	}

	public default void lor() {
		visitor().visitInsn(Opcodes.LOR);
	}


	// Bitwise XOR

	public default void ixor() {
		visitor().visitInsn(Opcodes.IXOR);
	}

	public default void lxor() {
		visitor().visitInsn(Opcodes.LXOR);
	}


	// Increment local integer variable

	public default void iinc(int varIndex, int increment) {
		visitor().visitIincInsn(varIndex, increment);
	}

}