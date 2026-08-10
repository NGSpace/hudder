package dev.ngspace.hudder.hudderv3.asm;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import dev.ngspace.hudder.compilers.utils.TextPos;
import dev.ngspace.hudder.exceptions.ExecutionException;
import dev.ngspace.hudder.hudderv3.asm.methods.ClassAccessMethodWriter;

public class V3MethodWriter extends ClassAccessMethodWriter {
	
	public V3ClassWriter classWriter;
	public String methodName;
	
	public V3MethodWriter(V3ClassWriter classWriter, String name, Class<?>[] parameters, Class<?> returntype,
			String signature, String[] exceptions) {
		super(classWriter.classWriter.visitMethod(Opcodes.ACC_PUBLIC, name,
				Type.getMethodDescriptor(returntype == null ? Type.VOID_TYPE : Type.getType(returntype),
						List.of(parameters).stream().map(Type::getType).toList().toArray(new Type[0])),
				signature, exceptions));
		this.classWriter = classWriter;
		this.methodName = name;
	}
	
	@Override
	public String getClassName() {
		return classWriter.classname;
	}
	
	public void throwRuntimeException(String exception) {
		newAndDup(RuntimeException.class);
		loadConstant(exception);
		callSpecial(RuntimeException.class, "<init>", "(Ljava/lang/String;)V", false);
		athrow();
	}
	
	public void throwExecutionException(String exception, TextPos pos) {
		newAndDup(ExecutionException.class);
		loadConstant(exception);
		loadConstantUnsafe(pos.line());
		loadConstantUnsafe(pos.column());
		callSpecial(ExecutionException.class, "<init>", "(Ljava/lang/String;II)V", false);
		athrow();
	}
}
