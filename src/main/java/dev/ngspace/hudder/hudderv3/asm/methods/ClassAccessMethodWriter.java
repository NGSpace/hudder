package dev.ngspace.hudder.hudderv3.asm.methods;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public abstract class ClassAccessMethodWriter extends VariableMethodWriter {

	protected ClassAccessMethodWriter(MethodVisitor methodVisitor) {
		super(methodVisitor);
	}
	
	public abstract String getClassName();
	

	public void getField(String name, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.GETFIELD, getClassName(),
				name, Type.getDescriptor(type));
	}

	public void putField(String name, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, getClassName(),
				name, Type.getDescriptor(type));
	}

	public void putStaticField(String name, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, getClassName(),
				name, Type.getDescriptor(type));
	}

	/**
	 * @deprecated use {@link #callSelf(String, boolean, Class, Class[])}
	 */
	@Deprecated(since = "10.4.0", forRemoval = false)
	public void callSelf(String name, String descriptor, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getClassName(), name, descriptor,
				isInterface);
	}
	
	public void callSelf(String name, boolean isInterface, Class<?> returntype, Class<?>... args) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getClassName(), name,
				getMethodDescriptor(returntype, args), isInterface);
	}
}
