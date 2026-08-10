package dev.ngspace.hudder.hudderv3.asm;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.asm.methods.VariableMethodWriter;

public class V3MethodWriter extends VariableMethodWriter {
	
	
	public V3ClassWriter classWriter;
	public String methodName;

	public V3MethodWriter(V3ClassWriter classWriter, String name, Class<?>[] parameters, Class<?> returntype,
			String signature, String[] exceptions) {
		super(classWriter.classWriter.visitMethod(Opcodes.ACC_PUBLIC, name,
				Type.getMethodDescriptor(returntype==null?Type.VOID_TYPE:Type.getType(returntype),
						List.of(parameters).stream().map(Type::getType).toList().toArray(new Type[0])),
				signature, exceptions));
		this.classWriter = classWriter;
		this.methodName = name;
		methodVisitor.visitCode();
	}



	public void initStringBuilder() {
		newAndDup(StringBuilder.class);
		callInit(StringBuilder.class, "()V");
	}

	public void getField(String name, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classWriter.classname,
				name, Type.getDescriptor(type));
	}

	public void getField(String name, Class<?> owner, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.GETFIELD, Type.getInternalName(owner),
				name, Type.getDescriptor(type));
	}

	public void getStaticField(String name, Class<?> owner, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(owner),
				name, Type.getDescriptor(type));
	}

	public void putField(String name, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, classWriter.classname,
				name, Type.getDescriptor(type));
	}

	public void putField(String name, Class<?> owner, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, Type.getInternalName(owner),
				name, Type.getDescriptor(type));
	}

	public void putStaticField(String name, Class<?> type) {
		methodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, classWriter.classname,
				name, Type.getDescriptor(type));
	}



	public void callSpecial(Class<?> type, String name, String sign, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(type), name, sign,
				isInterface);
	}
	public void callInit(Class<?> type, String sign) {
		callSpecial(type, "<init>", sign, false);
	}

	public void callInterface(Class<?> clazz, String name, String descriptor) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getInternalName(clazz), name, descriptor,
				true);
	}

	public void callStatic(Class<?> clazz, String name, String descriptor, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(clazz), name, descriptor,
				isInterface);
	}

	public void call(Class<?> clazz, String name, String descriptor, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(clazz), name, descriptor,
				isInterface);
	}

	public void callSelf(String name, String descriptor, boolean isInterface) {
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, classWriter.classname, name, descriptor,
				isInterface);
	}



	public void throwRuntimeException(String exception) {
		newAndDup(RuntimeException.class);
		loadConstant(exception);
		callSpecial(RuntimeException.class, "<init>", "(Ljava/lang/String;)V", false);
		methodVisitor.visitInsn(Opcodes.ATHROW);
	}



	public void throwExecutionException(String exception, TextPos pos) {
		throwExecutionException(exception, pos.line(), pos.column());
	}
	public void throwExecutionException(String exception, int line, int col) {
		newAndDup(ExecutionException.class);
		loadConstant(exception);
		loadConstantUnsafe(line);
		loadConstantUnsafe(col);
		callSpecial(ExecutionException.class, "<init>", "(Ljava/lang/String;II)V", false);
		methodVisitor.visitInsn(Opcodes.ATHROW);
	}
}
