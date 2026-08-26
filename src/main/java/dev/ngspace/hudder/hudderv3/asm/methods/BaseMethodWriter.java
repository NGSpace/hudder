package dev.ngspace.hudder.hudderv3.asm.methods;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public abstract class BaseMethodWriter implements MethodWriterJumpInsn, MethodWriterConstants,
		MethodWriterTypeInsn, MethodWriterMath {

	protected final MethodVisitor methodVisitor;
	public Label finalLabel = new Label();
	public int variableindex = 5;
	
	protected BaseMethodWriter(MethodVisitor methodVisitor) {
		this.methodVisitor = methodVisitor;
		methodVisitor.visitCode();
	}



	@Override
	public MethodVisitor visitor() {
		return methodVisitor;
	}

	public void end() {
		end(Opcodes.RETURN);
	}

	public void end(int Opcode) {
		putLabel(finalLabel);
		methodVisitor.visitInsn(Opcode);
		
		endNoInsn();
	}

	protected void endNoInsn() {
		methodVisitor.visitMaxs(0, 0);
		methodVisitor.visitEnd();
	}
	
	public void putLineNumber(int line, Label start) {
		methodVisitor.visitLineNumber(line, start);
	}
	
	public void tryCatchBlock(Consumer<Label> try_block, Consumer<Label> catch_block, Class<?> exception) {
		Label try_start = new Label();
		Label try_end = new Label();
		Label catch_handler = new Label();
		Label end = new Label();
		
		tryCatch(try_start, try_end, catch_handler, exception);
		
		putLabel(try_start);
		try_block.accept(try_end);
		putLabel(try_end);
		jumpto(end);
		
		putLabel(catch_handler);
		
		catch_block.accept(end);
		
		putLabel(end);
	}

	// -------------------------------------------------------------------------
	// CALLING
	// -------------------------------------------------------------------------

	/**
	 * @deprecated use {@link #callSpecial(Class, String, boolean, Class, Class...)}
	 */
	@Deprecated(since = "10.4.0", forRemoval = false)
	public void callSpecial(Class<?> type, String name, String descriptor, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(type), name, descriptor,
				isInterface);
	}
	/**
	 * @deprecated use {@link #callInit(Class, Class...)} or {@link #callInit(Class)}
	 */
	@Deprecated(since = "10.4.0", forRemoval = false)
	public void callInit(Class<?> type, String descriptor) {
		callSpecial(type, "<init>", descriptor, false);
	}

	/**
	 * @deprecated use {@link #callInterface(Class, String, Class, Class...)}
	 */
	@Deprecated(since = "10.4.0", forRemoval = false)
	public void callInterface(Class<?> clazz, String name, String descriptor) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getInternalName(clazz), name, descriptor,
				true);
	}
	
	/**
	 * @deprecated use {@link #callStatic(Class, String, boolean, Class, Class...)}
	 */
	@Deprecated(since = "10.4.0", forRemoval = false)
	public void callStatic(Class<?> clazz, String name, String descriptor, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(clazz), name, descriptor,
				isInterface);
	}
	
	/**
	 * @deprecated use {@link #call(Class, String, boolean, Class, Class...)}
	 */
	@Deprecated(since = "10.4.0", forRemoval = false)
	public void call(Class<?> clazz, String name, String descriptor, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(clazz), name, descriptor,
				isInterface);
	}
	
	public void callSpecial(Class<?> type, String name, boolean isInterface, @Nullable Class<?> returntype, Class<?>... args) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(type), name,
				getMethodDescriptor(returntype, args), isInterface);
	}
	public void callInit(Class<?> type, Class<?>... args) {
		callSpecial(type, "<init>", false, null, args);
	}
	public void callInit(Class<?> type) {
		callInit(type, new Class[0]);
	}

	public void callInterface(Class<?> clazz, String name, @Nullable Class<?> returntype, Class<?>... args) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getInternalName(clazz), name,
				getMethodDescriptor(returntype, args), true);
	}

	public void callStatic(Class<?> clazz, String name, boolean isInterface, @Nullable Class<?> returntype,
			Class<?>... args) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(clazz), name,
				getMethodDescriptor(returntype, args), isInterface);
	}

	public void call(Class<?> clazz, String name, boolean isInterface, @Nullable Class<?> returntype,
			Class<?>... args) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(clazz), name,
				getMethodDescriptor(returntype, args), isInterface);
	}

	public static String getMethodDescriptor(@Nullable Class<?> returntype, Class<?>[] args) {
		Type[] args_type = new Type[args.length];
		for (int i = 0;i<args_type.length;i++)
			args_type[i] = Type.getType(args[i]);
		return Type.getMethodDescriptor(returntype==null?Type.VOID_TYPE:Type.getType(returntype),args_type);
	}

	// -------------------------------------------------------------------------
	// LOAD
	// -------------------------------------------------------------------------

	public void iload(int index) {
		methodVisitor.visitVarInsn(Opcodes.ILOAD, index);
	}

	public void lload(int index) {
		methodVisitor.visitVarInsn(Opcodes.LLOAD, index);
	}

	public void fload(int index) {
		methodVisitor.visitVarInsn(Opcodes.FLOAD, index);
	}

	public void dload(int index) {
		methodVisitor.visitVarInsn(Opcodes.DLOAD, index);
	}

	public void aload(int index) {
		methodVisitor.visitVarInsn(Opcodes.ALOAD, index);
	}

	// -------------------------------------------------------------------------
	// STORE - automatically allocate
	// -------------------------------------------------------------------------

	public int istore() {
		methodVisitor.visitVarInsn(Opcodes.ISTORE, ++variableindex);
		return variableindex;
	}

	public int fstore() {
		methodVisitor.visitVarInsn(Opcodes.FSTORE, ++variableindex);
		return variableindex;
	}

	public int astore() {
		methodVisitor.visitVarInsn(Opcodes.ASTORE, ++variableindex);
		return variableindex;
	}

	/**
	 * long occupies two local-variable slots.
	 */
	public int lstore() {
		methodVisitor.visitVarInsn(Opcodes.LSTORE, ++variableindex);
		return variableindex++;
	}

	/**
	 * double occupies two local-variable slots.
	 */
	public int dstore() {
		methodVisitor.visitVarInsn(Opcodes.DSTORE, ++variableindex);
		return variableindex++;
	}

	// -------------------------------------------------------------------------
	// STORE - explicit index
	// -------------------------------------------------------------------------

	public void istore(int index) {
		methodVisitor.visitVarInsn(Opcodes.ISTORE, index);
	}

	public void lstore(int index) {
		methodVisitor.visitVarInsn(Opcodes.LSTORE, index);
	}

	public void fstore(int index) {
		methodVisitor.visitVarInsn(Opcodes.FSTORE, index);
	}

	public void dstore(int index) {
		methodVisitor.visitVarInsn(Opcodes.DSTORE, index);
	}

	public void astore(int index) {
		methodVisitor.visitVarInsn(Opcodes.ASTORE, index);
	}

	// -------------------------------------------------------------------------
	// RET
	// -------------------------------------------------------------------------

	public void ret(int index) {
		methodVisitor.visitVarInsn(Opcodes.RET, index);
	}

	// -------------------------------------------------------------------------
	// ARRAY LOAD
	// -------------------------------------------------------------------------

	public void iaload() {
		methodVisitor.visitInsn(Opcodes.IALOAD);
	}

	public void laload() {
		methodVisitor.visitInsn(Opcodes.LALOAD);
	}

	public void faload() {
		methodVisitor.visitInsn(Opcodes.FALOAD);
	}

	public void daload() {
		methodVisitor.visitInsn(Opcodes.DALOAD);
	}

	public void aaload() {
		methodVisitor.visitInsn(Opcodes.AALOAD);
	}

	public void baload() {
		methodVisitor.visitInsn(Opcodes.BALOAD);
	}

	public void caload() {
		methodVisitor.visitInsn(Opcodes.CALOAD);
	}

	public void saload() {
		methodVisitor.visitInsn(Opcodes.SALOAD);
	}

	// -------------------------------------------------------------------------
	// ARRAY STORE
	// -------------------------------------------------------------------------

	public void iastore() {
		methodVisitor.visitInsn(Opcodes.IASTORE);
	}

	public void lastore() {
		methodVisitor.visitInsn(Opcodes.LASTORE);
	}

	public void fastore() {
		methodVisitor.visitInsn(Opcodes.FASTORE);
	}

	public void dastore() {
		methodVisitor.visitInsn(Opcodes.DASTORE);
	}

	public void aastore() {
		methodVisitor.visitInsn(Opcodes.AASTORE);
	}

	public void bastore() {
		methodVisitor.visitInsn(Opcodes.BASTORE);
	}

	public void castore() {
		methodVisitor.visitInsn(Opcodes.CASTORE);
	}

	public void sastore() {
		methodVisitor.visitInsn(Opcodes.SASTORE);
	}
}
